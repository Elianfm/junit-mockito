package com.elianfm.junitapp.models;

import java.math.BigDecimal;

import com.elianfm.junitapp.exceptions.DineroInsuficienteException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Uso lombok para evitar código boilerplate
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cuenta {
    private String persona;
    private Banco banco;
    /*
    * Big Decimal es una clase que nos permite trabajar con números decimales de
    * alta precisión. La diferencia con double o float es que estos últimos no
    * pueden representar correctamente algunos números decimales. Por esto es
    * recomendable usar BigDecimal para operaciones financieras.
    */
    private BigDecimal saldo;

    public Cuenta(String persona, BigDecimal saldo) {
        this.persona = persona;
        this.saldo = saldo;
    }

    public void debito(BigDecimal monto) {
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if(nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero insuficiente");
        }
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }
}
