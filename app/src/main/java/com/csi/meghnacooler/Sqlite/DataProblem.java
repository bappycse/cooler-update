package com.csi.meghnacooler.Sqlite;

/**
 * Created by Jahid on 22/7/19.
 */
public class DataProblem {
    String problemName;
    String problemId;

    public DataProblem(String problemName, String problemId) {
        this.problemName = problemName;
        this.problemId = problemId;
    }
    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }
}
