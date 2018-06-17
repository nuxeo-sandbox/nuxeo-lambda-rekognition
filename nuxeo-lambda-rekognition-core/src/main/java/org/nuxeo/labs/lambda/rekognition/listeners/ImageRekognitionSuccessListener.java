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

package org.nuxeo.labs.lambda.rekognition.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.labs.lambda.rekognition.worker.ImageRekognitionResultWork;
import org.nuxeo.runtime.api.Framework;

import java.io.Serializable;
import java.util.Map;

import static org.nuxeo.lambda.core.LambdaService.LAMBDA_RESPONSE_KEY;
import static org.nuxeo.lambda.core.LambdaService.PARAMETERS_KEY;

public class ImageRekognitionSuccessListener implements PostCommitEventListener {

    private static final Log log = LogFactory.getLog(ImageRekognitionSuccessListener.class);

    @Override
    public void handleEvent(EventBundle eventBundle) {
        for (Event event : eventBundle) {
            handleEvent(event);
        }
    }

    private void handleEvent(Event event) {
        EventContext ctx = event.getContext();
        if (ctx == null) {
            return;
        }

        Map<String, Serializable> params =
                (Map<String, Serializable>) ctx.getProperty(PARAMETERS_KEY);

        JSONObject object = (JSONObject) ctx.getProperty(LAMBDA_RESPONSE_KEY);
        if (params == null || object == null) {
            throw new NuxeoException("Response is Empty");
        }

        final String repoName = (String) params.get("repository");
        final String docId = (String) params.get("docId");

        ImageRekognitionResultWork work = new ImageRekognitionResultWork(repoName, docId, object);
        WorkManager workManager = Framework.getService(WorkManager.class);
        workManager.schedule(work, WorkManager.Scheduling.IF_NOT_SCHEDULED, true);
    }
}