/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.experiment;


public class StringExperimentResult implements ExperimentResult{
    private int id;
    private String msg;

    public StringExperimentResult(){
    }

    public StringExperimentResult(int id, String msg){
        this.id = id;
        this.msg = msg;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getMessage() {
        return getMsg();
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean equals(Object other){
        return this.id == ((StringExperimentResult)other).getId();
    }
}
