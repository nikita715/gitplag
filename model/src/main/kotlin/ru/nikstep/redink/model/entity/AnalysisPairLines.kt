package ru.nikstep.redink.model.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Lines of two files suspected for plagiarism
 */
@Entity
data class AnalysisPairLines(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(nullable = false)
    val from1: Int,

    @Column(nullable = false)
    val to1: Int,

    @Column(nullable = false)
    val from2: Int,

    @Column(nullable = false)
    val to2: Int,

    @Column(nullable = false)
    val fileName1: String,

    @Column(nullable = false)
    val fileName2: String,

    @Column(nullable = false)
    val sha1: String,

    @Column(nullable = false)
    val sha2: String,

    @ManyToOne
    @field:JsonBackReference
    @get:JsonBackReference
    @JoinColumn(nullable = false)
    val analysisPair: AnalysisPair
) {
    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) = this::class.isInstance(other)
}