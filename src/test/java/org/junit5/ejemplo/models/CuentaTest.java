package org.junit5.ejemplo.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit5.ejemplo.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {
    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest() {
        this.cuenta = new Cuenta("Cristian", new BigDecimal("100000.12345"));
        System.out.println("Iniciando el método");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el Método de prueba");
    }

    @DisplayName("Nombre de la cuenta corriente!")
    @Test
    void testNombreCuenta() {
        //Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("100000.12345"));
        //cuenta.setPersona("Cristian");
        String esperado = "Cristian";
        String actual = cuenta.getPersona();
        assertNotNull(actual, () -> "La cuenta no puede ser null");
        assertEquals(esperado, actual, () -> "El nombre de la cuenta no es el que se esperaba:  se esperaba " + esperado + " pero fue " + actual);
        assertTrue(actual.equals("Cristian"), () -> "Nombre cuenta esperado debe ser igual a la real o actual");
    }

    @DisplayName("Saldo de la cuenta corriente!")
    @Test
    void testSaldoCuenta() {
        assertNotNull(cuenta.getSaldo());
        assertEquals(100000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Referencias que sean iguales, método equals")
    @Test
    void testReferenciaCuenta() {
        cuenta = new Cuenta("Cristian Doe", new BigDecimal("1234.443"));
        Cuenta cuenta2 = new Cuenta("Cristian Doe", new BigDecimal("1234.443"));
        //assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        //cuenta = new Cuenta("Cristian", new BigDecimal("100.12345"));
        cuenta.debito(new BigDecimal(90));
        assertNotNull(cuenta.getSaldo());
        assertEquals(10, cuenta.getSaldo().intValue());
        assertEquals("10.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        //Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("100.12345"));
        cuenta.credito(new BigDecimal(90));
        assertNotNull(cuenta.getSaldo());
        assertEquals(190, cuenta.getSaldo().intValue());
        assertEquals("190.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        //Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("100.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1000));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Jonh Doe", new BigDecimal("1000000"));
        Cuenta cuenta2 = new Cuenta("Cristian Doe", new BigDecimal("900000"));
        Banco banco = new Banco();
        banco.setNombre("Bancolombia");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500000));
        assertEquals("400000", cuenta2.getSaldo().toPlainString());
        assertEquals("1500000", cuenta1.getSaldo().toPlainString());
    }

    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll")
    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Jonh Doe", new BigDecimal("1000000"));
        Cuenta cuenta2 = new Cuenta("Cristian Doe", new BigDecimal("900000"));
        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Bancolombia");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500000));
        assertAll(
                () -> assertEquals("400000", cuenta2.getSaldo().toPlainString(), () -> "El valor del saldo de cuenta 2 no es el esperado"),
                () -> assertEquals("1500000", cuenta1.getSaldo().toPlainString(), () -> "El valor del saldo de cuenta 1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(), () -> "El banco no tiene las cuentas esperadas"),
                () -> assertEquals("Bancolombia", cuenta1.getBanco().getNombre()),
                () -> assertEquals("Cristian Doe", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Cristian Doe"))
                        .findFirst()
                        .get().getPersona()),
                () -> assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("Cristian Doe")))
        );
    }
}