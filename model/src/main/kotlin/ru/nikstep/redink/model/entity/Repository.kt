package ru.nikstep.redink.model.entity

import javax.persistence.*

@Entity
@Table(name = "repository")
class Repository(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1,

    @ManyToOne
    @JoinColumn(name = "owner")
    val owner: User,

    @Column(name = "pattern")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "repository_pattern", joinColumns = [JoinColumn(name = "repository")])
    val filePatterns: List<String> = listOf(),

    val name: String,
    val repoGithubId: Long
)