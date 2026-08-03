package ca.notification.organisations.service.domain

data class NewOrganisation(
    val name: String
)

data class Organisation(
    val id: TypedUUID<Organisation>,
    val name: String
) {
    companion object {
        fun createNew(name: String) = NewOrganisation(name)

        fun from(id: TypedUUID<Organisation>, name: String) = Organisation(id, name)

        fun withId(newOrganisation: NewOrganisation, id: TypedUUID<Organisation>) = Organisation(
            id = id,
            name = newOrganisation.name
        )
    }
}

class OrganisationNotFoundException(id: TypedUUID<Organisation>) : RuntimeException("Organisation with id $id not found")
