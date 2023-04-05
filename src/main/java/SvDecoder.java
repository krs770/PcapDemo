import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PcapPacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class SvDecoder {

    private static final int dateSetSize = 64;

    public Optional<SvPacket> decode(PcapPacket packet) {
        try {
            byte[] data = packet.getRawData();
            int length = data.length;

            SvPacket result = new SvPacket();

            result.setMacDst(byteArrayToMac(data, 0));
            result.setMacSrc(byteArrayToMac(data, 6));

            result.setType(byteArrayToString(data, 12));

            result.getDataSet().setInstIa(byteArrayToInt(data, length - dateSetSize) / 100.0);
            result.getDataSet().setQIa(byteArrayToInt(data, length - dateSetSize + 4));
            result.getDataSet().setInstIb(byteArrayToInt(data, length - dateSetSize + 8) / 100.0);
            result.getDataSet().setQIb(byteArrayToInt(data, length - dateSetSize + 12));
            result.getDataSet().setInstIc(byteArrayToInt(data, length - dateSetSize + 16) / 100.0);
            result.getDataSet().setQIc(byteArrayToInt(data, length - dateSetSize + 20));
            result.getDataSet().setInstIn(byteArrayToInt(data, length - dateSetSize + 24) / 100.0);
            result.getDataSet().setQIn(byteArrayToInt(data, length - dateSetSize + 28));

            result.getDataSet().setInstUa(byteArrayToInt(data, length - dateSetSize + 32) / 1000.0);
            result.getDataSet().setQUa(byteArrayToInt(data, length - dateSetSize + 36));
            result.getDataSet().setInstUb(byteArrayToInt(data, length - dateSetSize + 40) / 1000.0);
            result.getDataSet().setQUb(byteArrayToInt(data, length - dateSetSize + 44));
            result.getDataSet().setInstUc(byteArrayToInt(data, length - dateSetSize + 48) / 1000.0);
            result.getDataSet().setQUc(byteArrayToInt(data, length - dateSetSize + 52));
            result.getDataSet().setInstUn(byteArrayToInt(data, length - dateSetSize + 56) / 1000.0);
            result.getDataSet().setQIn(byteArrayToInt(data, length - dateSetSize + 60));

            result.setSvId(byteArrayToSvId(data, 33));
            result.setSmpCount(byteArrayToSmpCnt(data, 45));
            result.setAppID(byteArrayToShort(data, 14));
            result.setConfRev(byteArrayToInt(data, 49));
            result.setSmpSynch(byteArrayToSmpSynch(data, 55));

            return Optional.of(result);
        } catch (Exception e) {
            log.error("Cannot parse sv packet");
        }
        return Optional.empty();
    }

    public static String byteArrayToMac(byte[] b, int offset) {
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                b[offset],
                b[1 + offset],
                b[2 + offset],
                b[3 + offset],
                b[4 + offset],
                b[5 + offset]
        );
    }

    @SneakyThrows
    public static String byteArrayToSvId(byte[] b, int offset) {
        byte[] resultByte = {b[offset],
                b[1 + offset],
                b[2 + offset],
                b[3 + offset],
                b[4 + offset],
                b[5 + offset],
                b[6 + offset],
                b[7 + offset],
                b[8 + offset],
                b[9 + offset]};
        return new String(resultByte, StandardCharsets.UTF_8);
    }

    public static short byteArrayToShort(byte[] b, int offset) {
        byte[] resultByte = {b[offset], b[offset + 1]};
        return ByteBuffer.wrap(resultByte).getShort();
    }

    public static String byteArrayToString(byte[] b, int offset) {
        return String.format("0x%02x%02x", b[offset], b[offset + 1]);
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        return b[offset + 3] & 0xFF | (b[offset + 2] & 0xFF) << 8 | (b[offset + 1] & 0xFF) << 16 | (b[offset] & 0xFF) << 24;
    }

    public static int byteArrayToSmpCnt(byte[] b, int offset) {
        return b[offset + 1] & 0xFF | (b[offset] & 0xFF) << 8;
    }

    public static int byteArrayToSmpSynch(byte[] b, int offset) {
        return b[offset] & 0xFF;
    }

}
