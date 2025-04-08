package com.elianfm.junitapp.models;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

//import org.junit.jupiter.api.Assertions; // JUnit 5 se puede usar de esta forma pero no es necesario
import static org.junit.jupiter.api.Assertions.*; // Importamos todas las assertions estáticas

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assumptions.*;

import com.elianfm.junitapp.exceptions.DineroInsuficienteException;

/*
* TIPOS DE ASSERTIONS
* - assertEquals(esperado, real) -> Verifica que los dos valores sean iguales
* - assertNotEquals(esperado, real) -> Verifica que los dos valores no sean iguales
* - assertTrue(condicion) -> Verifica que la condición sea verdadera
* - assertFalse(condicion) -> Verifica que la condición sea falsa
* - assertNull(objeto) -> Verifica que el objeto sea nulo
* - assertNotNull(objeto) -> Verifica que el objeto no sea nulo
* - assertSame(esperado, real) -> Verifica que los dos objetos sean el mismo
* - assertNotSame(esperado, real) -> Verifica que los dos objetos no sean el mismo
* - assertArrayEquals(esperado, real) -> Verifica que los dos arreglos sean iguales
* - assertIterableEquals(esperado, real) -> Verifica que los dos iterables sean iguales
* - assertLinesMatch(esperado, real) -> Verifica que las líneas de los dos streams sean iguales
* - assertThrows(excepcion, ejecutable) -> Verifica que se lance una excepción
* - assertTimeout(duracion, ejecutable) -> Verifica que el ejecutable se ejecute en un tiempo determinado
* - assertTimeoutPreemptively(duracion, ejecutable) -> Verifica que el ejecutable se ejecute en un tiempo determinado y lo cancela si excede el tiempo
* - fail(mensaje) -> Hace que el test falle
* - fail() -> Hace que el test falle sin mensaje
* - assertAll(executables) -> Ejecuta varios ejecutables y verifica que todos sean exitosos
* - assertAll(mensaje, executables) -> Ejecuta varios ejecutables y verifica que todos sean exitosos con un mensaje
* etc.
*/

/*
 * Las clases y métodos de test deben ser default para que JUnit pueda acceder
 */
/*
 * @TestInstance(TestInstance.Lifecycle.PER_CLASS) -> Se ejecuta una instancia por
 * clase de test, es decir, se ejecuta una sola vez por clase de test. Es útil
 * para compartir información entre los métodos de test, pero no es recomendable
 * porque puede afectar el rendimiento y la independencia de los tests.
 */
// @TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CuentaTest {
    // @BeforeEach -> Se ejecuta antes de cada test, funcionan también en clases
    // anidadas
    // @AfterEach -> Se ejecuta después de cada test, funcionan también en clases
    // anidadas
    // @BeforeAll -> Se ejecuta antes de todos los tests
    // @AfterAll -> Se ejecuta después de todos los tests

    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest() {
        System.out.println("Inicializando el método de test");
        this.cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));
    }

    @AfterEach
    void tearDown(TestInfo testInfo, TestReporter testReporter) {
        System.out.println("Finalizando el método de test");
        // ideal usar testinfo y testreporter para imprimir mensajes combinado
        // con aftereach y beforeeach para tener más información en cada test
        testReporter.publishEntry("Finalizando el test " + testInfo.getDisplayName());
        testInfo.getTags().forEach(tag -> testReporter.publishEntry(tag));

    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test completo");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test completo");
    }

    /*
     * CONDICIONALES
     * 
     * @EnabledOnOs(OS.LINUX) -> Habilita el test si el sistema operativo es Linux
     * 
     * @EnabledOnJre(JRE.JAVA_8) -> Habilita el test si la versión de Java es 8
     * 
     * @EnabledIfSystemProperty(named = "user.name", matches = "elian") -> Habilita
     * el test si el nombre de usuario es "elian"
     * 
     * @DisabledIfSystemProperty(named = "user.name", matches = "elian") ->
     * Deshabilita el test si el nombre de usuario es "elian"
     * 
     * @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches =
     * "C:\\Program Files\\Java\\jdk-11") -> Habilita el test si la variable de
     * entorno JAVA_HOME es igual a "C:\\Program Files\\Java\\jdk-11"
     * 
     * @DisabledIfEnvironmentVariable(named = "JAVA_HOME", matches =
     * "C:\\Program Files\\Java\\jdk-11") -> Deshabilita el test si la variable de
     * entorno JAVA_HOME es igual a "C:\\Program Files\\Java\\jdk-11"
     * 
     * @EnabledIf("2 * 3 == 6") -> Habilita el test si la condición es verdadera
     * 
     * @DisabledIf("2 * 3 == 6") -> Deshabilita el test si la condición es verdadera
     * 
     * @EnabledIf("'CI' == systemEnvironment.get('ENV')") -> Habilita el test si la
     * variable de entorno ENV es igual a "CI"
     * 
     * @DisabledIf("'CI' == systemEnvironment.get('ENV')") -> Deshabilita el test si
     * la variable de entorno ENV es igual a "CI"
     * 
     * @EnabledIf("'PRO'.equals(systemProperty.get('ENV'))") -> Habilita el test si
     * la variable de sistema ENV es igual a "PRO"
     * 
     * @DisabledIf("'PRO'.equals(systemProperty.get('ENV'))") -> Deshabilita el test
     * si la variable de sistema ENV es igual a "PRO"
     * 
     * @EnabledIf(value =
     * "systemProperty.get('os.name').toLowerCase().contains('windows')",
     * loadContext = true) -> Habilita el test si el sistema operativo es Windows
     * 
     * @DisabledIf(value =
     * "systemProperty.get('os.name').toLowerCase().contains('windows')",
     * loadContext = true) -> Deshabilita el test si el sistema operativo es Windows
     * etc.
     * 
     * Funcionan solo si se ejecuta la clase de test completa, no si se ejecuta un
     * solo test
     */

    // Nested -> Permite agrupar tests en una clase de test
    // Útil para organizar los tests y hacerlos más legibles
    @Nested
    @DisplayName("Pruebas parametrizadas")
    class PruebasParametrizadas {
        @Test
        @EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
        void testDesarrollo() {
            System.out.println("testDesarrollo");
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENV", matches = "prod")
        void testProduccion() {
            System.out.println("testProduccion");
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "elian")
        void testUsuarioElian() {
            System.out.println("testUsuarioElian");
        }

        @Test
        @EnabledIfSystemProperties({
                @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*"),
                @EnabledIfSystemProperty(named = "os.name", matches = "Windows 10")
        })
        void testPropiedadesSistema() {
            System.out.println("testPropiedadesSistema");
        }

        @Test
        @EnabledIf("2 * 3 == 6")
        void testMultiplicacion() {
            System.out.println("testMultiplicacion");
        }

        @Test
        @EnabledIf("'CI' == systemEnvironment.get('ENV')")
        void testEnv() {
            System.out.println("testEnv");
        }

        @Test
        @EnabledIf("'PRO'.equals(systemProperty.get('ENV'))")
        void testEnv2() {
            System.out.println("testEnv2");
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testNumeroProcesadores() {
            System.out.println("testNumeroProcesadores");
        }
    }

    @Test
    @EnabledOnOs({ OS.WINDOWS })
    void testSoloEnWindows() {
        System.out.println("testSoloEnWindows");
    }

    @Test
    @EnabledOnOs({ OS.LINUX })
    void testSoloEnLinux() {
        System.out.println("testSoloEnLinux");
    }

    @Test
    void imprimirSystemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.vendor", matches = "Oracle Corporation")
    void testUsuarioElian() {
        System.out.println("testVendorOracle");
    }

    @Test
    void imprimirVariablesAmbiente() {
        Map<String, String> getenv = System.getenv();
        getenv.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
    void testNumeroProcesadores() {
        System.out.println("testNumeroProcesadores");
    }

    // La anotación @Test indica que el método es un test unitario
    // La anotacion @DisplayName permite asignar un nombre al test
    @Test
    @DisplayName("Probando nombre de la cuenta")
    void testNombreCuenta() {
        // Instanciamos un objeto de la clase Cuenta
        // Importante pasar el big decimal como string para evitar errores de precisión
        // ya que double y float no pueden representar correctamente algunos números
        // decimales

        // Acá no es necesario instanciar la cuenta porque se hace en el método
        // initMetodoTest
        // Cuenta cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));
        // cuenta.setPersona("Elian");

        String esperado = "Elian";
        String real = cuenta.getPersona();

        // Verificamos que no sea nulo y enviamos un mensaje en caso de que falle
        assertNotNull(real, () -> "La cuenta no puede ser nula");

        // Verificamos que el nombre de la cuenta sea igual a "Elian"
        // Si no es así, el test fallará, si el método no tuviera assert,
        // el test se consideraría exitoso sin importar el resultado
        // assertEquals(esperado, real); sin mensaje
        assertEquals(esperado, real, () -> "El nombre de la cuenta no es el esperado");
        assertTrue(real.equals("Elian"), () -> "El nombre de la cuenta no es el esperado");

        // También se puede sin lambda, peero no se recomienda
        // porque se instancia el mensaje aunque el test sea exitoso
        // lo que puede afectar el rendimiento
        assertEquals(esperado, real, "El nombre de la cuenta no es el esperado");

    }

    @Test
    @DisplayName("Probando saldo de la cuenta")
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));
        assertNotNull(cuenta.getSaldo());
        // Verificamos que el saldo de la cuenta sea igual a 1000.12345
        // Usamos el método equals de BigDecimal para comparar los valores
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());

        // Con assertFalse verificamos que el saldo no sea menor a 0
        // Se realiza de esta forma porque BigDecimal no tiene un método compareTo que
        // reciba un double. El < 0 se realiza con compareTo(BigDecimal.ZERO) que
        // devuelve -1 si es menor, 0 si es igual y 1 si es mayors
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("Probando referencia de la cuenta")
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));
        Cuenta cuenta2 = new Cuenta("Elian", new BigDecimal("1000.12345"));

        // Con assertEquals verificamos que los dos objetos sean iguales
        // teniendo en cuenta que lombok sobreescribe el método equals
        // y hashCode para comparar los valores de los atributos,
        // sin lombok tendríamos que sobreescribir estos métodos.
        assertEquals(cuenta, cuenta2);

    }

    /*
     * TDD (Test Driven Development) es una técnica de desarrollo de software que
     * consiste en escribir primero los tests y luego el código que los haga pasar.
     */
    @Test
    @DisplayName("Probando debito de la cuenta")
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));

        // Realizamos un débito de 100
        cuenta.debito(new BigDecimal(100));

        // Verificamos que el saldo no sea nulo
        assertNotNull(cuenta.getSaldo());

        // Verificamos que el saldo sea igual a 900.12345
        assertEquals(900.12345, cuenta.getSaldo().doubleValue());

        // Verificamos que el saldo no sea menor a 0
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("Probando debito de la cuenta con dinero insuficiente")
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));

        // Realizamos un crédito de 100
        cuenta.credito(new BigDecimal(100));

        // Verificamos que el saldo no sea nulo
        assertNotNull(cuenta.getSaldo());

        // Verificamos que el saldo sea igual a 1100.12345
        assertEquals(1100.12345, cuenta.getSaldo().doubleValue());

        // Verificamos que el saldo no sea menor a 0
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);

    }

    @Test
    @DisplayName("Probando debito de la cuenta con dinero insuficiente")
    void dineroInsuficienteExceptionTest() {
        Cuenta cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));

        // Verificamos que se lance una excepción al intentar debitar más dinero del
        // que hay en la cuenta
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });

        // Verificamos que el mensaje de la excepción sea igual a "Dinero insuficiente"
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    @DisplayName("Probando transferencia de dinero")
    void testTransferirDinero() {
        Cuenta cuenta1 = new Cuenta("Elian", new BigDecimal("1000.12345"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("3000.12345"));

        Banco banco = new Banco("Banco de la nación");

        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        // Verificamos que el saldo de la cuenta 1 sea igual a 1500.12345
        assertEquals(1500.12345, cuenta1.getSaldo().doubleValue());

        // Verificamos que el saldo de la cuenta 2 sea igual a 1500.12345
        assertEquals(2500.12345, cuenta2.getSaldo().doubleValue());

    }

    @Test
    @DisplayName("Probando relación entre banco 🏦 y cuentas")
    // @Disabled -> Deshabilita el test, útil para ignorar tests que no se quieren
    // ejecutar, por ejemplo si no están completos o se esta trabajando en ellos
    @Disabled
    void testRelacionBancoCuentas() {

        // fail() -> Se usa para verificar que un test falle
        fail();

        Cuenta cuenta1 = new Cuenta("Elian", new BigDecimal("1000.12345"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("3000.12345"));

        Banco banco = new Banco("Banco de la nación");

        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        // Verificamos que el banco tenga 2 cuentas
        assertEquals(2, banco.getCuentas().size());

        banco.removeCuenta(cuenta1);

        // Verificamos que el banco tenga 1 cuenta
        assertEquals(1, banco.getCuentas().size());

        // Verificamos que el banco de la cuenta1 sea igual a "Banco de la nación"
        assertEquals("Banco de la nación", cuenta1.getBanco().getNombre());

        // Verificamos que Andres este en el banco a través de la cuenta
        assertEquals("Andres", banco.getCuentas().stream().filter(c -> c.getPersona().equals("Andres")).findFirst()
                .get().getPersona());

        // Lo mismo que el anterior pero con assertTrue
        assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Andres")));
    }

    // assertAll si falla uno FALLAN TODOS
    // pero se ejecutan todos y se muestran todos los errores
    // assertAll se usa para verificar varias condiciones en un solo test
    @Test
    @DisplayName("Probando relación entre banco 🏦 y cuentas con assertAll")
    void testRelacionBancoCuentasConAssertAll() {
        Cuenta cuenta1 = new Cuenta("Elian", new BigDecimal("1000.12345"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("3000.12345"));

        Banco banco = new Banco("Banco de la nación");

        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        assertAll(
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> {
                    banco.removeCuenta(cuenta1);
                    assertEquals(1, banco.getCuentas().size());
                },
                () -> assertEquals("Banco de la nación", cuenta1.getBanco().getNombre()),
                () -> assertEquals("Andres",
                        banco.getCuentas().stream().filter(c -> c.getPersona().equals("Andres")).findFirst()
                                .get().getPersona()),
                () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Andres"))));
    }

    /*
     * ASSUMPTIONS -> Se usan para verificar condiciones antes de ejecutar un test
     * Esto es similar a usar @EnabledIfEnvironmentVariablw
     */
    @Test
    void testSaldoCuentaAssumptions() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));

        // Verificamos que la condición sea verdadera
        // Si es falsa, el test se salta
        assumeTrue(esDev);

        Cuenta cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());

        // o usando lambda
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
    }

    /*
     * @RepeatedTest -> Permite repetir un test un número determinado de veces
     * se usa para probar la repetibilidad de un test y cuando hay variables
     * o parámetros que cambian en cada ejecución, se reemplaza @Test
     * por @RepeatedTest
     * 
     */
    @DisplayName("Probando debito cuenta repetir")
    @RepeatedTest(value = 5, name = "{displayName} - Repetición {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        Cuenta cuenta = new Cuenta("Elian", new BigDecimal("1000.12345"));

        if (info.getCurrentRepetition() == 3) {
            System.out.println("Estamos en la repetición " + info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    /*
     * @Nested -> Permite agrupar tests en una clase de test
     * 
     * @Tag -> Permite asignar etiquetas a los tests, útil para filtrar los tests
     * Para filtrar los tests se usa el comando mvn test -Dgroups=nombreEtiqueta
     * o mvn test -Dtags=nombreEtiqueta en caso de usar tags
     */
    @Tag("param")
    @Tag("Pueden ser varias etiquetas")
    @DisplayName("Pruebas parametrizadas")
    @Nested
    class PruebasParametrizadasTest {
        /*
         * PARAMETRIZACIÓN DE TESTS
         * Se usa para probar un método con diferentes valores
         * Se usa @ParameterizedTest en lugar de @Test
         * Se usa @ValueSource para pasar los valores
         * Se usa @MethodSource para pasar un método que devuelva los valores
         * Se usa @CsvSource para pasar los valores separados por coma
         * Se usa @CsvFileSource para pasar los valores desde un archivo CSV
         * Se usa @ArgumentsSource para pasar argumentos
         * Se usa @NullSource para pasar valores nulos
         * Se usa @EmptySource para pasar valores vacíos
         * Se usa @NullAndEmptySource para pasar valores nulos y vacíos
         * Se usa @EnumSource para pasar valores de un enum
         * Se usa @EnumSource.Mode para pasar valores de un enum con un modo específico
         * Se usa @EnumSource.Names para pasar valores de un enum por nombre
         * Se usa @EnumSource.Mode.EXCLUDE para excluir valores de un enum
         * Se usa @EnumSource.Mode.MATCH_ALL para incluir todos los valores de un enum
         * Se usa @EnumSource.Mode.MATCH_ANY para incluir cualquier valor de un enum
         */
        @ParameterizedTest
        @ValueSource(strings = { "100", "200", "300", "500", "700", "1000" })
        void testDebitoCuentaConValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        // con ints
        @ParameterizedTest
        @ValueSource(ints = { 100, 200, 300, 500, 700, 1000 })
        void testDebitoCuentaConValueSourceInt(int monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        // con csv
        @ParameterizedTest
        @CsvSource({ "100, 900", "200, 800", "300, 700", "500, 500", "700, 300", "1000, 0" })
        void testDebitoCuentaConCsvSource(String monto, String esperado) {
            cuenta.debito(new BigDecimal(monto));
            assertEquals(new BigDecimal(esperado), cuenta.getSaldo());
        }

        // con un archivo data.csv
        @ParameterizedTest
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaConCsvFileSource(String monto, String esperado) {
            cuenta.debito(new BigDecimal(monto));
            assertEquals(new BigDecimal(esperado), cuenta.getSaldo());
        }

        // con un method source
        @ParameterizedTest
        @MethodSource("montoList")
        void testDebitoCuentaConMethodSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        static List<String> montoList() {
            return List.of("100", "200", "300", "500", "700", "1000");
        }

        // asignando la cuenta
        @ParameterizedTest(name = "Realizando el test {index} -> monto: {0}, saldo: {1}")
        @CsvSource({ "100, 900", "200, 800", "300, 700", "500, 500", "700, 300", "1000, 0" })
        void testDebitoCuentaConCsvSourceAsignandoCuenta(String saldo, String monto) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

    }

    @Nested
    class PruebasTestInfoYTestReporter {
        @Test
        @Tag("testInfo")
        @DisplayName("Probando test info")
        void testInfo(TestInfo testInfo) {
            System.out.println(testInfo.getDisplayName());
            System.out.println(testInfo.getTestMethod().orElse(null).getName());
            System.out.println(testInfo.getTestClass().orElse(null).getName());
            System.out.println(testInfo.getTags());
            System.out.println(testInfo.getTags().contains("param"));
        }

        @Test
        @DisplayName("Probando test reporter")
        void testReporter(TestReporter testReporter) {
            testReporter.publishEntry("developer", "Elian");
            testReporter.publishEntry("developer", "Andres");
        }
    }

    @Nested
    class PruebasConTimeout {
        @Test
        @DisplayName("Probando timeout")
        void testTimeout() {
            assertTimeout(Duration.ofSeconds(3), () -> {
                Thread.sleep(2000);
            });
        }

        @Test
        @DisplayName("Probando timeout preemptively")
        void testTimeoutPreemptively() {
            // timeout preemptively cancela el test si excede el tiempo
            assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
                Thread.sleep(2000);
            });
        }

        @Test
        @Timeout(5)
        void testTimeoutConAnotacion() throws InterruptedException {
            TimeUnit.SECONDS.sleep(6);
        }

        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
        void testTimeoutConAnotacionYUnidad() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(600);
        }
    }


    /*
     * Maven Surefire Plugin -> Permite configurar los tests en Maven
     * Por ejemplo, se puede configurar para que ejecute los tests que tengan
     * una etiqueta específica.
     * Útil cuando no se tiene un IDE que permita ejecutar los tests, todo
     * se hace desde la terminal.
     * 
     * <plugin>
     *      <groupId>org.apache.maven.plugins</groupId>
     *      <artifactId>maven-surefire-plugin</artifactId>
     *      <version>3.0.0-M5</version>
     *     <configuration>
     *         <groups>param</groups> -> Ejecuta los tests con la etiqueta param
     *    </configuration>
     * </plugin>
     * 
     * m
     * 
     */
}
