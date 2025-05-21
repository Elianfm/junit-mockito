package com.elianfm.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.elianfm.test.springboot.app.Data.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.elianfm.test.springboot.app.exceptions.InsufficientFundsException;
import com.elianfm.test.springboot.app.models.Account;
import com.elianfm.test.springboot.app.models.Bank;
import com.elianfm.test.springboot.app.repositories.AccountRepository;
import com.elianfm.test.springboot.app.repositories.BankRepository;
import com.elianfm.test.springboot.app.services.AccountService;
import com.elianfm.test.springboot.app.services.AccountServiceImpl;


// SpringBootTest es una anotación que se utiliza para indicar que es una 
// prueba de integración de Spring Boot. Esta anotación incluye junit y
// mockito, por lo que no es necesario incluirlas en el pom.xml
@SpringBootTest
class AppApplicationTests {

	//@Mock
	//@MockBean // A diferencia de @Mock, @MockBean crea un mock que se inyecta
	// en el contexto de Spring.
	@MockitoBean // Desde Spring Boot 3.4.0 @MockBean es marcado como obsoleto
	// y se recomienda usar @MockitoBean
	AccountRepository accountRepository;

	//@Mock
	//@MockBean
	@MockitoBean
	BankRepository bankRepository;

	//@InjectMocks
	@Autowired
	AccountService accountService;

	@BeforeEach
	void setUp() {
		// Aquí comentamos la creación de los mocks para que se creen
		// con las anotaciones @MockBean y @Autowired
		// accountRepository = mock(AccountRepository.class);
		// bankRepository = mock(BankRepository.class);
		// accountService = new AccountServiceImpl(accountRepository, bankRepository);	
	}

	@Test
	void contextLoads() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());
		when(accountRepository.findById(2L)).thenReturn(createAccount002());
		when(bankRepository.findById(1L)).thenReturn(createBank001());


		BigDecimal fromBalance = accountService.checkBalance(1L);
		BigDecimal toBalance = accountService.checkBalance(2L);

		assertEquals("1000.00", fromBalance.toPlainString());
		assertEquals("2000.00", toBalance.toPlainString());


		accountService.transfer(1L, 2L, new BigDecimal("100.00"), 1L);
		fromBalance = accountService.checkBalance(1L);
		toBalance = accountService.checkBalance(2L);

		int totalTransactions = accountService.checkTotalTransactions(1L);
		assertEquals(1, totalTransactions);

		assertEquals("900.00", fromBalance.toPlainString());
		assertEquals("2100.00", toBalance.toPlainString());

		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(3)).findById(2L);
		verify(accountRepository, times(2)).save(any(Account.class));

		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository, times(1)).save(any(Bank.class));

		verify(accountRepository, times(6)).findById(anyLong());
		verify(accountRepository, never()).findAll();

	}

	@Test
	void contextLoads2() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());
		when(accountRepository.findById(2L)).thenReturn(createAccount002());
		when(bankRepository.findById(1L)).thenReturn(createBank001());


		BigDecimal fromBalance = accountService.checkBalance(1L);
		BigDecimal toBalance = accountService.checkBalance(2L);

		assertEquals("1000.00", fromBalance.toPlainString());
		assertEquals("2000.00", toBalance.toPlainString());

		assertThrows(InsufficientFundsException.class, () -> {
			accountService.transfer(1L, 2L, new BigDecimal("1200.00"), 1L);
		});

		fromBalance = accountService.checkBalance(1L);
		toBalance = accountService.checkBalance(2L);

		int totalTransactions = accountService.checkTotalTransactions(1L);
		assertEquals(0, totalTransactions);

		// Se mantiene el saldo original porque la excepción fue lanzada
		// y no se realizó la transferencia
		assertEquals("1000.00", fromBalance.toPlainString());
		assertEquals("2000.00", toBalance.toPlainString());

		verify(accountRepository, times(3)).findById(1L);
		verify(accountRepository, times(3)).findById(2L);
		verify(accountRepository, never()).save(any(Account.class));

		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository, times(0)).save(any(Bank.class));

		verify(accountRepository, times(6)).findById(anyLong());
		verify(accountRepository, never()).findAll();

	}

	@Test
	void contextLoads3() {
		when(accountRepository.findById(1L)).thenReturn(createAccount001());

		Account account1 = accountService.findById(1L);
		Account account2 = accountService.findById(1L);

		assertSame(account1, account2);
		assertTrue(account1 == account2); // Lo mismo que assertSame
		
		assertEquals("Elian", account1.getPerson());
		assertEquals("Elian", account2.getPerson());

		verify(accountRepository, times(2)).findById(1L);

	}

	@Test 
	void testFindAll(){
		// Given
		List<Account> accounts = List.of(
				createAccount001().orElseThrow(),
				createAccount002().orElseThrow()
		);
		when(accountRepository.findAll()).thenReturn(accounts);

		// When
		List<Account> result = accountService.findAll();

		// Then
		assertFalse(result.isEmpty());
		assertEquals(2, result.size());
		assertTrue(result.contains(createAccount001().orElseThrow()));
		assertEquals("Elian", result.get(0).getPerson());
		assertEquals("Julián", result.get(1).getPerson());
		assertEquals("1000.00", result.get(0).getBalance().toPlainString());
		assertEquals("2000.00", result.get(1).getBalance().toPlainString());

		verify(accountRepository, times(1)).findAll();
		
	}

	@Test
	void testCreate() {

		// Given
		Account account = new Account(null, "Picasso", new BigDecimal(1111.00));
		when(accountRepository.save(any())).then( invocation -> {
			Account acc = invocation.getArgument(0);
			acc.setId(3L);
			return acc;
		});

		// When
		Account result = accountService.save(account);

		// Then
		assertNotNull(result);
		assertEquals(3L, result.getId());
		assertEquals("Picasso", result.getPerson());
		assertEquals("1111", result.getBalance().toPlainString());

		verify(accountRepository, times(1)).save(any());
		
	}

}
