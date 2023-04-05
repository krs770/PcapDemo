import lombok.*;

@Setter
@Getter
@Data
public class SvPacket {

    private String macDst;

    private String macSrc;

    private String type;
    private short appID;

    private String svId;

    private int smpCount;

    private int confRev;

    private int smpSynch;

    private DataSet dataSet = new DataSet();

    public SvPacket() {
    }

    public SvPacket(String macDst, String macSrc, short appID, String svId, int smpCount, int confRev, int smpSynch, DataSet dataSet) {
        this.macDst = macDst;
        this.macSrc = macSrc;
        this.appID = appID;
        this.svId = svId;
        this.smpCount = smpCount;
        this.confRev = confRev;
        this.smpSynch = smpSynch;
        this.dataSet = dataSet;
    }


    @Getter
    @Setter
    @Data
    public class DataSet {

        private double instIa;
        private int qIa;
        private double instIb;
        private int qIb;
        private double instIc;
        private int qIc;
        private double instIn;
        private int qIn;

        private double instUa;
        private int qUa;
        private double instUb;
        private int qUb;
        private double instUc;
        private int qUc;
        private double instUn;
        private int qUn;

    }
}

