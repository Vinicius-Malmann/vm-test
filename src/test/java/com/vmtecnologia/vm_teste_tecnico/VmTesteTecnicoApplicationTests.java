package com.vmtecnologia.vm_teste_tecnico;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class VmTesteTecnicoApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}
}