package com.rochatransportes.service;

import com.rochatransportes.model.Nota;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class FilaNotasProcessamento {

    private final Queue<Nota> filaNotas = new LinkedList<>();

    public synchronized void enfileirar(Nota nota) {
        if (nota == null) return;
        boolean jaExiste = filaNotas.stream()
                .anyMatch(n -> n.getId() != null && n.getId().equals(nota.getId()));
        if (!jaExiste) {
            filaNotas.offer(nota);
        }
    }

    public synchronized Nota desenfileirar() {
        return filaNotas.poll();
    }

    public synchronized Nota consultarProxima() {
        return filaNotas.peek();
    }

    public synchronized boolean removerPorId(Long id) {
        if (id == null) return false;
        return filaNotas.removeIf(n -> id.equals(n.getId()));
    }

    public synchronized List<Nota> listarFila() {
        return new ArrayList<>(filaNotas);
    }

    public synchronized int tamanho() {
        return filaNotas.size();
    }

    public synchronized boolean estaVazia() {
        return filaNotas.isEmpty();
    }
}