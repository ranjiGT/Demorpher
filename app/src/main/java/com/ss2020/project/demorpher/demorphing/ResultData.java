
package com.ss2020.project.demorpher.demorphing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultData {

    @SerializedName("accompliceFace")
    @Expose
    private String accompliceFace;
    @SerializedName("evaluation")
    @Expose
    private String evaluation;
    @SerializedName("score")
    @Expose
    private Double score;

    public String getAccompliceFace() {
        return accompliceFace;
    }

    public void setAccompliceFace(String accompliceFace) {
        this.accompliceFace = accompliceFace;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

}
