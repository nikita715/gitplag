package ru.nikstep.redink.entity

import javax.persistence.*

@Entity
class SourceCode(
    @ManyToOne
    @JoinColumn(name = "p_user")
    var user: User,
    @ManyToOne
    @JoinColumn(name = "repo")
    var repo: Repository,
    var fileName: String,
    var fileText: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}