package com.uma.example.springuma.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;

public class MedicoControllerMockMvcIT extends AbstractIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Medico medico;

    @BeforeEach
    void setUp() {
        medico = new Medico();
        medico.setId(1L);
        medico.setDni("835");
        medico.setNombre("Miguel");
        medico.setEspecialidad("Ginecologia");
    }

    private void crearMedico(Medico medico) throws Exception {
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated());
    }

    @Test
@DisplayName("Crear medico y recuperarlo por ID")
void saveMedico_RecuperaPorId() throws Exception {
    crearMedico(medico);

    mockMvc.perform(get("/medico/" + medico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(medico.getId()))
            .andExpect(jsonPath("$.nombre").value("Miguel"))
            .andExpect(jsonPath("$.dni").value("835"))
            .andExpect(jsonPath("$.especialidad").value("Ginecologia"));
}

@Test
@DisplayName("Buscar medico por DNI")
void getMedicoByDni() throws Exception {
    crearMedico(medico);

    mockMvc.perform(get("/medico/dni/" + medico.getDni()))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.nombre").value("Miguel"))
            .andExpect(jsonPath("$.dni").value("835"));
}

@Test
@DisplayName("Buscar medico por DNI inexistente")
void getMedicoByDni_NotFound() throws Exception {
    mockMvc.perform(get("/medico/dni/999999"))
            .andExpect(status().isNotFound());
}

@Test
@DisplayName("Actualizar medico")
void updateMedico() throws Exception {
    crearMedico(medico);

    medico.setNombre("Carlos");
    medico.setEspecialidad("Cardiologia");

    mockMvc.perform(put("/medico")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(medico)))
            .andExpect(status().isNoContent());

    mockMvc.perform(get("/medico/" + medico.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Carlos"))
            .andExpect(jsonPath("$.especialidad").value("Cardiologia"));
}

}
