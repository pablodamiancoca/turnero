package com.turnero.config;

import com.turnero.model.Empresa;
import com.turnero.repository.EmpresaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatosDemo(EmpresaRepository empresaRepository) {
        return args -> {
            if (!empresaRepository.existsBySlug("demo")) {
                Empresa demo = new Empresa("demo", "Cafetería Demo");
                demo.setLogoUrl("https://dummyimage.com/160x160/2563eb/ffffff.png&text=DEMO");
                demo.setColorPrimario("#2563eb");
                demo.setColorSecundario("#1e293b");
                demo.setPrefijo("A");
                demo.setMensajeBienvenida("¡Bienvenido a Cafetería Demo!");
                empresaRepository.save(demo);
            }
        };
    }
}
