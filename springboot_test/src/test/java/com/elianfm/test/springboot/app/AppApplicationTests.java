package com.elianfm.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.elianfm.test.springboot.app.Data.*;

import java.math.BigDecimal;

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
		verify(accountRepository, times(2)).update(any(Account.class));

		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository, times(1)).update(any(Bank.class));

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
		verify(accountRepository, never()).update(any(Account.class));

		verify(bankRepository, times(2)).findById(1L);
		verify(bankRepository, times(0)).update(any(Bank.class));

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

}
