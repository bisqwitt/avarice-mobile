package com.avaricious.utility.runData;

import java.util.ArrayList;
import java.util.List;

public class RunData {

    public String runId;
    public List<ScoreChangeData> scoreChangeData = new ArrayList<>();

    public RunData setRunId(String runId) {
        this.runId = runId;
        return this;
    }

}
