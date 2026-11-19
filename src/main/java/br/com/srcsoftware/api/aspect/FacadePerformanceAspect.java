package br.com.srcsoftware.api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class FacadePerformanceAspect {

    // Intercepta qualquer método public dentro de classes no pacote de facade
    @Around("execution(* br.com.srcsoftware.api.atlantis.facade..*(..))")
    public Object medirTempoEExecutar(ProceedingJoinPoint joinPoint) throws Throwable {
        String nomeMetodo = joinPoint.getSignature().toShortString();
        log.info("Facade [Início]: {}", nomeMetodo);
        
        long inicio = System.nanoTime();
        try {
            Object resultado = joinPoint.proceed(); // Executa o método do Facade
            long tempoGasto = System.nanoTime() - inicio;
            
            log.info("Facade [Sucesso]: {} | Tempo gasto: {} segundos", nomeMetodo, tempoGasto / 1_000_000_000.0);
            return resultado;
        } catch (Throwable e) {
            long tempoGasto = System.nanoTime() - inicio;
            log.error("Facade [Erro]: {} após {} segundos | Motivo: {}", nomeMetodo, tempoGasto / 1_000_000_000.0, e.getMessage());
            throw e; // Relança a exceção original (mantendo o ApplicationException)
        }
    }
    
    // Intercepta qualquer método public dentro de classes no pacote de facade
    @Around("execution(* br.com.srcsoftware.api.sistema.facade..*(..))")
    public Object medirTempoEExecutarSistema(ProceedingJoinPoint joinPoint) throws Throwable {
        String nomeMetodo = joinPoint.getSignature().toShortString();
        log.info("Facade [Início]: {}", nomeMetodo);
        
        long inicio = System.nanoTime();
        try {
            Object resultado = joinPoint.proceed(); // Executa o método do Facade
            long tempoGasto = System.nanoTime() - inicio;
            
            log.info("Facade [Sucesso]: {} | Tempo gasto: {} segundos", nomeMetodo, tempoGasto / 1_000_000_000.0);
            return resultado;
        } catch (Throwable e) {
            long tempoGasto = System.nanoTime() - inicio;
            log.error("Facade [Erro]: {} após {} segundos | Motivo: {}", nomeMetodo, tempoGasto / 1_000_000_000.0, e.getMessage());
            throw e; // Relança a exceção original (mantendo o ApplicationException)
        }
    }
}