/*
 * (C) Copyright 2006-2018 Nuxeo (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *     michael vachette
 */
package org.nuxeo.labs.lambda.rekognition.worker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.runtime.api.Framework;


public class ImageRekognitionResultWork extends AbstractWork {

    private static final Log log = LogFactory.getLog(ImageRekognitionResultWork.class);

    protected final JSONObject response;

    public ImageRekognitionResultWork(String repository, String docId, JSONObject response) {
        super(repository + ":" + docId + ":ingest-image-rekognition");
        setDocument(repository, docId);
        this.response = response;
    }

    @Override
    public String getTitle() {
        return id;
    }

    @Override
    public void work() {
        setStatus("Starting");

        setProgress(Progress.PROGRESS_INDETERMINATE);

        openSystemSession();

        DocumentModel doc = session.getDocument(new IdRef(docId));

        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext octx = new OperationContext();
        octx.setInput(doc);
        octx.setCoreSession(session);
        octx.put("rekognition", response);
        OperationChain chain = new OperationChain("ProcessImageRekognitionChain");
        chain.add("javascript.image_rekognition_mapper");
        try {
            doc = (DocumentModel) as.run(octx, chain);
        } catch (OperationException e) {
            throw new NuxeoException(e);
        }
        session.saveDocument(doc);
        commitOrRollbackTransaction();
        setStatus("Done");
    }
}
