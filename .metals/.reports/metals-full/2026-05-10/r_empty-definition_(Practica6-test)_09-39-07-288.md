error id: file:///C:/Users/Usuario/Documents/UNIVERSIDAD/INFRAESTRUCTURA/BLOQUE02/Practica6Alumnos/src/test/java/com/uma/example/springuma/integration/PacienteControllerMockMvcIT.java:_empty_/JsonPath#
file:///C:/Users/Usuario/Documents/UNIVERSIDAD/INFRAESTRUCTURA/BLOQUE02/Practica6Alumnos/src/test/java/com/uma/example/springuma/integration/PacienteControllerMockMvcIT.java
empty definition using pc, found symbol in pc: _empty_/JsonPath#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 5764
uri: file:///C:/Users/Usuario/Documents/UNIVERSIDAD/INFRAESTRUCTURA/BLOQUE02/Practica6Alumnos/src/test/java/com/uma/example/springuma/integration/PacienteControllerMockMvcIT.java
text:
```scala
package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.MedicoService;
import com.uma.example.springuma.model.Paciente;

public class PacienteControllerMockMvcIT extends AbstractIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MedicoService medicoService;

    Paciente paciente;
    Medico medico;

    @BeforeEach
    void setUp() {
        medico = new Medico();
        medico.setNombre("Miguel");
        medico.setId(1L);
        medico.setDni("835");
        medico.setEspecialidad("Ginecologo");

        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNombre("Maria");
        paciente.setDni("888");
        paciente.setEdad(20);
        paciente.setCita("Ginecologia");
        paciente.setMedico(this.medico);
    }
    private void crearMedico(Medico medico) throws Exception {
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated());
    }
    private void crearPaciente(Paciente paciente) throws Exception {
        mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated());
    }

    private void getPacienteById(Long id, Paciente expected) throws Exception {
        mockMvc.perform(get("/paciente/" + id))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").value(expected));
    }



    @Test
    @DisplayName("Crear paciente y recuperarlo por ID pasado por parametro")
    void savePaciente_RecuperaPacientePorId() throws Exception {
        crearMedico(medico);
        crearPaciente(paciente);

        // Obtener paciente por ID y verificar campos
        mockMvc.perform(get("/paciente/" + paciente.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(paciente.getId()))
                .andExpect(jsonPath("$.nombre").value(paciente.getNombre()))
                .andExpect(jsonPath("$.dni").value(paciente.getDni()));
    }

@Test
    @DisplayName("Actualizar un paciente existente")
    void updatePaciente_CambiaDatosPaciente() throws Exception {
        crearMedico(medico);
        
        // 1. Creamos el paciente y CAPTURAMOS la respuesta del servidor
        MvcResult result = mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andReturn();

        // 2. Extraemos el paciente real con su ID generado
        String contenido = result.getResponse().getContentAsString();
        Paciente pacientePersistido = objectMapper.readValue(contenido, Paciente.class);

        // 3. Modificamos el objeto que SÍ tiene el ID correcto
        pacientePersistido.setNombre("Maria Modificado");
        pacientePersistido.setEdad(21);

        mockMvc.perform(put("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(pacientePersistido)))
                .andExpect(status().isOk());

        // 4. Verificamos usando el ID real
        mockMvc.perform(get("/paciente/" + pacientePersistido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maria Modificado"))
                .andExpect(jsonPath("$.edad").value(21));
    }

    @Test
    @DisplayName("Eliminar un paciente")
    void deletePaciente_YaNoExiste() throws Exception {
        crearMedico(medico);
        
        // 1. Creamos y capturamos ID real
        MvcResult result = mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated())
                .andReturn();
        
        String contenido = result.getResponse().getContentAsString();
        long idReal = JsonP@@ath.read(contenido, "$.id");

        // 2. Eliminamos usando el ID que nos dio la base de datos
        mockMvc.perform(delete("/paciente/" + idReal))
                .andExpect(status().isOk());

        // 3. Verificamos que ya no existe
        mockMvc.perform(get("/paciente/" + idReal))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Listar pacientes de un médico")
    void getPacientesByMedico_DevuelveLista() throws Exception {
        crearMedico(medico);
        crearPaciente(paciente);

        // Suponiendo que el endpoint es /paciente/medico/{medicoId}
        mockMvc.perform(get("/paciente/medico/" + medico.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value(paciente.getNombre()));
    }

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/JsonPath#