package org.junit5.ejemplo.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit5.ejemplo.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;


class CuentaTest {
    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest() {
        this.cuenta = new Cuenta("Cristian", new BigDecimal("100.12345"));
        System.out.println("Iniciando el método");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el Método de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterClass() {
        System.out.println("Finalizando el test");
    }

    @Tag("Cuenta")
    @Nested
    @DisplayName("Probando atributos de cuenta corriente")
    class CuentaTestNombreySaldo {
        @DisplayName("Nombre")
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

        @DisplayName("Saldo")
        @Test
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(100.12345, cuenta.getSaldo().doubleValue());
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
    }

    @Nested
    class CuentaOperaciones {
        @Tag("Cuenta")
        @Test
        void testDebitoCuenta() {
            //cuenta = new Cuenta("Cristian", new BigDecimal("100.12345"));
            cuenta.debito(new BigDecimal(90));
            assertNotNull(cuenta.getSaldo());
            assertEquals(10, cuenta.getSaldo().intValue());
            assertEquals("10.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("Cuenta")
        @Test
        void testCreditoCuenta() {
            //Cuenta cuenta = new Cuenta("Cristian", new BigDecimal("100.12345"));
            cuenta.credito(new BigDecimal(90));
            assertNotNull(cuenta.getSaldo());
            assertEquals(190, cuenta.getSaldo().intValue());
            assertEquals("190.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("Cuenta")
        @Tag("Banco")
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
    }

    @Tag("Cuenta")
    @Tag("Exceptions")
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

    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll")
    @Tag("Cuenta")
    @Tag("Banco")
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

    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersdionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testJdk8() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_8)
        void testNoJdk8() {
        }
    }

    @Nested
    class SystemPropertiesTes {
        @Test
        void testPrintSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "1.8")
        void testJavaVersion() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev∫")
        void testDev() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }
    }

    @Nested
    class VariableAmbiente {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "HOME", matches = "/Users/cristianblandon")
        void testHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv() {

        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testProdDisabled() {
        }
    }

    @DisplayName("Saldo de la cuenta corriente DEV!")
    @Test
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(100.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Saldo de la cuenta corriente DEV2!")
    @Test
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(100.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Probando método Reapeat en Débiuto Cuenta")
    @RepeatedTest(value = 5, name = "Repetición número {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepeated(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("Estamos en la repetición: " + info.getCurrentRepetition());
        }
        //cuenta = new Cuenta("Cristian", new BigDecimal("100.12345"));
        cuenta.debito(new BigDecimal(90));
        assertNotNull(cuenta.getSaldo());
        assertEquals(10, cuenta.getSaldo().intValue());
        assertEquals("10.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadas {
        @ParameterizedTest(name = "numero {index} valor {0} {argumentsWithNames}")
        @ValueSource(strings = {"90", "100", "200", "300", "500", "700", "1000"})
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} valor {0} {argumentsWithNames}")
        @CsvSource({"1,90", "2,100", "3,200", "4,300", "5,500", "6,700", "7,1000"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} valor {0} {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} valor {0} {argumentsWithNames}")
        @MethodSource("montoList")
        void testDebitoCuentaMethodSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} valor {0} {argumentsWithNames}")
        @CsvSource({"200,100, Cristian,cristian", "250,200,Nathalia,nathalia", "299,300,Juan,juan", "400,500,Cata,cata", "750,700,Lorem,Ipsum", "1000,1000, Cata,Cata"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} valor {0} {argumentsWithNames}")
        @CsvFileSource(resources = "/data1.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    static List<String> montoList() {
        return Arrays.asList("90", "100", "200", "300", "500", "700", "1000");
    }
}