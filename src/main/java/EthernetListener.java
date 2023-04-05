import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class EthernetListener {

    static {
        try {
            for (PcapNetworkInterface nic : Pcaps.findAllDevs()) log.info("Found NIC: {}", nic);
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isActive = true;

    public void pause() {
        isActive = false;
    }

    @Setter
    private String nicName;

    private PcapHandle handle;

    private final List<PacketListener> listeners = new CopyOnWriteArrayList<>();

    private final PacketListener defaultPacketListener = packet -> {
        listeners.forEach(listener -> listener.gotPacket(packet));
    };

    @SneakyThrows
    public void start() {
        if (handle == null) {
            initializeNetworkIntefrace();

            if (handle != null) {

                String filter = "ether proto 0x88ba && ether dst 01:0C:CD:04:00:01";
                handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

                Thread captureThread = new Thread(() -> {
                    while (isActive) {
                        try {
                            log.info("Starting packet capture");
                            handle.loop(0, defaultPacketListener);
                        } catch (PcapNativeException | InterruptedException | NotOpenException e) {
                            throw new RuntimeException(e);
                        }
                        log.info("Packet capture finished");
                    }
                    System.out.println("FINISHED");
                });
                captureThread.start();
            }
        }
    }

    @SneakyThrows
    private void initializeNetworkIntefrace() {

        Optional<PcapNetworkInterface> nic = Pcaps.findAllDevs().stream()
                .filter(i -> nicName.equals(i.getDescription()))
                .findFirst();

        if (nic.isPresent()) {
            handle = nic.get().openLive(1500, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
            log.info("Network handler created: {}", nic);
        } else {
            log.error("Network interface is not found");
        }
    }

    public void addListener(PacketListener listener) {
        listeners.add(listener);
    }
}


