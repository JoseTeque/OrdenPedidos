package joseguerra.ordereat.modelo;

import java.util.List;

public class MyResponse {
    private Long multicast_id;
    public int succes;
    private int failure;
    private int canonical_ids;
    private List<Results>  myResults;

    public MyResponse(Long multicast_id, int succes, int failure, int canonical_ids, List<Results> myResults) {
        this.multicast_id = multicast_id;
        this.succes = succes;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.myResults = myResults;
    }

    public Long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(Long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSucces() {
        return succes;
    }

    public void setSucces(int succes) {
        this.succes = succes;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<Results> getMyResults() {
        return myResults;
    }

    public void setMyResults(List<Results> myResults) {
        this.myResults = myResults;
    }
}
