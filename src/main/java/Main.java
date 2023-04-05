import javax.swing.table.TableRowSorter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        EthernetListener ethernetListener = new EthernetListener();
        ethernetListener.setNicName("Intel(R) Dual Band Wireless-AC 8265");

        SvDecoder svDecoder = new SvDecoder();

        CopyOnWriteArraySet<SvPacket> set1 = new CopyOnWriteArraySet<>();

        ethernetListener.addListener(packet -> {
            Optional<SvPacket> svPacket = svDecoder.decode(packet);
            if (svPacket.isPresent()) {

                List<SvPacket> list = svPacket.stream().toList();
                set1.addAll(list);
            }
        });

        ethernetListener.start();

        Thread.sleep(10000L);

        ethernetListener.pause();

        for (SvPacket element : set1) {
            System.out.println(element);
        }
        System.out.println(set1.size());
    }
}

