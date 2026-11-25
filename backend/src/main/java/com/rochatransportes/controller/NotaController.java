package com.rochatransportes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rochatransportes.model.Nota;
import com.rochatransportes.service.NotaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notas")
@CrossOrigin(origins = "http://localhost:4200")
public class NotaController {
    
    @Autowired
    private NotaService notaService;

    @GetMapping
    public ResponseEntity<List<Nota>> getAll() {
        return ResponseEntity.ok(notaService.findAll());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Nota>> getPendentes() {
        return ResponseEntity.ok(notaService.findNotasPendentes());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Nota>> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notaService.findAllPaginated(page, size));
    }

    // --- ENDPOINTS DA FILA DE EXPEDIÇÃO (FIFO) ---

    @GetMapping("/fila")
    public ResponseEntity<List<Nota>> listarFila() {
        return ResponseEntity.ok(notaService.listarFilaProcessamento());
    }

    @GetMapping("/fila/proxima")
    public ResponseEntity<Nota> verProximaDaFila() {
        Nota proxima = notaService.verProximaDaFila();
        if (proxima == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(proxima);
    }

    @PostMapping("/fila/processar")
    public ResponseEntity<?> processarProximaDaFila() {
        try {
            Nota processada = notaService.processarProximaNota();
            return ResponseEntity.ok(processada);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/enfileirar")
    public ResponseEntity<Nota> enfileirarNota(@PathVariable Long id) {
        try {
            Nota enfileirada = notaService.enfileirarNotaExistente(id);
            return ResponseEntity.ok(enfileirada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ---------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<Nota> getById(@PathVariable Long id) {
        return notaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Nota> create(@Valid @RequestBody Nota nota) {
        Nota saved = notaService.save(nota);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Nota> update(@PathVariable Long id, @Valid @RequestBody Nota nota) {
        try {
            Nota updated = notaService.update(id, nota);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            notaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}