rootProject.name = "Asuha"
include("core")
include("platforms")
include("platforms:minestom")
findProject(":platforms:minestom")?.name = "minestom"
