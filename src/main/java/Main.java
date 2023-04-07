import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class Main {

    static boolean faultA = false;
    static boolean faultB = false;
    static boolean faultC = false;


    public static void main(String[] args) throws InterruptedException {

        ArrayList<String> faultedPhases = new ArrayList<>(3);


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

        Thread.sleep(12000L);

        if (set1.size() == 12000) {
            ethernetListener.pause();
        }

        double Inorm = set1.stream()
                .limit(80)
                .map(el -> el.getDataSet().getInstIa())
                .max(Comparator.naturalOrder())
                .get();

        double Unorm = set1.stream()
                .limit(80)
                .map(el -> el.getDataSet().getInstUa())
                .max(Comparator.naturalOrder())
                .get();

        double IaAvar = 0;

        CopyOnWriteArraySet<Double> set2 = new CopyOnWriteArraySet<>();

        for (SvPacket packet : set1) {
            if (Math.abs(packet.getDataSet().getInstIa()) > Inorm) {
                set2.add(packet.getDataSet().getInstIa());
                faultA = true;

            }
            if (Math.abs(packet.getDataSet().getInstIb()) > Inorm) {
                faultB = true;
            }
            if (Math.abs(packet.getDataSet().getInstIc()) > Inorm) {
                faultC = true;
            }
        }

        double I_A_avr = set2.stream()
                .max(Comparator.naturalOrder())
                .get();

        if (faultA) faultedPhases.add("Phase A");
        if (faultB) faultedPhases.add("Phase B");
        if (faultC) faultedPhases.add("Phase C");
        if ((!faultA) && (!faultB) && (faultC)) {
            faultedPhases.add(" ");
            log.info("No short circuit");
        }

        Thread.sleep(1000L);
        
        for(SvPacket element: set1){
            System.out.println(element);
        }

        System.out.println("Current normal mode = " + Inorm / Math.sqrt(2));
        System.out.println("Voltage normal mode = " + Unorm / Math.sqrt(2));
        System.out.println("IaAvar = " + I_A_avr / Math.sqrt(2));
        System.out.println("Short circuit was found in next phases: " + faultedPhases);
    }
}

