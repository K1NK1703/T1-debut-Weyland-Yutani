package ru.romanov.weyland.yutani.bishop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication(scanBasePackages = {
        "ru.romanov.weyland.yutani.bishop",
        "ru.romanov.weyland.yutani.synthetic"
})
@EnableAsync
@EnableScheduling
public class BishopPrototypeApplication {

    public static void main(String[] args) {
        printBishopBanner();

        SpringApplication.run(BishopPrototypeApplication.class, args);
    }

    private static void printBishopBanner() {
        System.out.println();
        System.out.println("██████╗ ██╗███████╗██╗  ██╗ ██████╗ ██████╗ ");
        System.out.println("██╔══██╗██║██╔════╝██║  ██║██╔═══██╗██╔══██╗");
        System.out.println("██████╔╝██║███████╗███████║██║   ██║██████╔╝");
        System.out.println("██╔══██╗██║╚════██║██╔══██║██║   ██║██╔═══╝ ");
        System.out.println("██████╔╝██║███████║██║  ██║╚██████╔╝██║     ");
        System.out.println("╚═════╝ ╚═╝╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═╝     ");
        System.out.println();
        System.out.println("██████╗ ██████╗  ██████╗ ████████╗ ██████╗ ████████╗██╗   ██╗██████╗ ███████╗");
        System.out.println("██╔══██╗██╔══██╗██╔═══██╗╚══██╔══╝██╔═══██╗╚══██╔══╝╚██╗ ██╔╝██╔══██╗██╔════╝");
        System.out.println("██████╔╝██████╔╝██║   ██║   ██║   ██║   ██║   ██║    ╚████╔╝ ██████╔╝█████╗  ");
        System.out.println("██╔═══╝ ██╔══██╗██║   ██║   ██║   ██║   ██║   ██║     ╚██╔╝  ██╔═══╝ ██╔══╝  ");
        System.out.println("██║     ██║  ██║╚██████╔╝   ██║   ╚██████╔╝   ██║      ██║   ██║     ███████╗");
        System.out.println("╚═╝     ╚═╝  ╚═╝ ╚═════╝    ╚═╝    ╚═════╝    ╚═╝      ╚═╝   ╚═╝     ╚══════╝");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════════════════════");
        System.out.println();
    }
}
