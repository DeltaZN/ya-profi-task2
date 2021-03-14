package com.example.demo.controller

import com.example.demo.repository.Note
import com.example.demo.repository.NoteRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException

data class NoteChangeRequest(
    val title: String?,
    val content: String,
)

data class NoteResponse(
    val id: Long,
    val title: String?,
    val content: String,
)

@RestController
@RequestMapping("/notes")
class NotesController(
    val noteRepository: NoteRepository,
) {

    @Value("\${title.absence.n.length}")
    val n = 0

    @PostMapping
    fun addNote(@RequestBody payload: NoteChangeRequest): NoteResponse {
        val note = Note(0, payload.title, payload.content)
        noteRepository.save(note)
        return NoteResponse(note.id, note.title, note.content)
    }

    @PutMapping("/{id}")
    fun editNote(@PathVariable id: Long, @RequestBody payload: NoteChangeRequest): NoteResponse {
        val note = noteRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Note with id $id - not found") }
        note.apply {
            title = payload.title
            content = payload.content
        }
        noteRepository.save(note)
        return NoteResponse(note.id, note.title, note.content)
    }

    @GetMapping("/{id}")
    fun getNote(@PathVariable id: Long): NoteResponse {
        val note = noteRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Note with id $id - not found") }
        val title = if (note.title == null) note.content.substring(0, n) else note.title
        return NoteResponse(note.id, title, note.content)
    }

    @GetMapping
    fun getNotesByQuery(@RequestParam(defaultValue = "") query: String): List<NoteResponse> = noteRepository.findByTitleContainsOrContentContains(query, query)
        .map { n ->
            val title = if (n.title == null) n.content.substring(0, this.n) else n.title
            NoteResponse(n.id, title, n.content)
        }

    @DeleteMapping("/{id}")
    fun deleteNote(@PathVariable id: Long): NoteResponse {
        val note = noteRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Note with id $id - not found") }
        noteRepository.delete(note)
        return NoteResponse(note.id, note.title, note.content)
    }

}