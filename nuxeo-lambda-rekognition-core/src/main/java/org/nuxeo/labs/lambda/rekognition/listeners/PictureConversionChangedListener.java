/*
 * (C) Copyright 2015-2018 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Michael Vachette
 */
package org.nuxeo.labs.lambda.rekognition.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.storage.sql.S3BinaryManager;
import org.nuxeo.ecm.platform.picture.api.adapters.MultiviewPicture;
import org.nuxeo.lambda.core.LambdaInput;
import org.nuxeo.lambda.core.LambdaService;
import org.nuxeo.runtime.api.Framework;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.PICTURE_FACET;

public class PictureConversionChangedListener implements PostCommitEventListener {

    private static final Log log = LogFactory.getLog(PictureConversionChangedListener.class);

    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            handleEvent(event);
        }
    }

    protected void handleEvent(Event event) {
        EventContext ectx = event.getContext();
        if (!(ectx instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext docCtx = (DocumentEventContext) ectx;
        DocumentModel doc = docCtx.getSourceDocument();
        if (!doc.hasFacet(PICTURE_FACET) || doc.isProxy()) {
            return;
        }

        MultiviewPicture mvp = doc.getAdapter(MultiviewPicture.class);
        Blob blob = mvp.getView("Medium").getBlob();

        if (blob == null) {
            return;
        }

        //filter non S3 blobs
        BlobManager blobManager = Framework.getService(BlobManager.class);
        BlobProvider blobProvider = blobManager.getBlobProvider(blob);
        if (!(blobProvider instanceof S3BinaryManager)) {
            return;
        }

        Map<String, Serializable> params = new HashMap<>();
        params.put("docId", doc.getId());
        params.put("repository", doc.getRepositoryName());

        LambdaService service = Framework.getService(LambdaService.class);

        Map<String, Serializable> input = new HashMap<>();
        input.put("key", blob.getDigest());
        input.put("bucket", Framework.getProperty("nuxeo.s3storage.bucket"));

        try {
            service.scheduleCall("nuxeo-picture-rekognition", params, new LambdaInput(input));
        } catch (Exception e) {
            throw new NuxeoException(e);
        }
    }
}
