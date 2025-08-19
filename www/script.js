// Role data type structure

// Role icons mapping
function getRoleIcon(roleName) {
  switch (roleName.toLowerCase()) {
    case "werewolf":
      return "🐺"
    case "seer":
      return "🔮"
    case "guardian":
      return "🛡️"
    case "villager":
      return "👤"
    case "major":
      return "👑"
    case "spectator":
      return "👁️"
    default:
      return "❓"
  }
}

// Role colors for visual distinction
function getRoleColorClass(roleName) {
  switch (roleName.toLowerCase()) {
    case "werewolf":
      return "werewolf"
    case "seer":
      return "seer"
    case "guardian":
      return "guardian"
    case "villager":
      return "villager"
    case "major":
      return "major"
    case "spectator":
      return "spectator"
    default:
      return "default"
  }
}

const roleData = async () => (await fetch("http://10.22.65.107:8080/role/randomize"))

// Simulate fetching data from backend
async function loadRoleData() {
  // Simulate API delay
  const data = await (await roleData()).json()
  if (data.error === "No roles available") {
    const spectatorData = {
      roleId: "spectator-" + Date.now(),
      roleName: "spectator",
      roleDescription: "Watch the game unfold and enjoy the mystery",
      available: 1,
    }
    displayRole(spectatorData)
  } else displayRole(data)
}

// Display role information
function displayRole(data) {
  const loadingElement = document.getElementById("loading")
  const roleCardElement = document.getElementById("role-card")
  const roleIconElement = document.getElementById("role-icon")
  const roleNameElement = document.getElementById("role-name")
  const roleDescriptionElement = document.getElementById("role-description")
  const roleIdElement = document.getElementById("role-id")
  const roleAvailableElement = document.getElementById("role-available")

  // Hide loading and show role card
  loadingElement.classList.add("hidden")
  roleCardElement.classList.remove("hidden")

  console.log('test', data)

  // Set role icon
  roleIconElement.textContent = getRoleIcon(data.roleName)

  // Set role name with appropriate color
  roleNameElement.textContent = data.roleName
  roleNameElement.className = `role-name ${getRoleColorClass(data.roleName)}`

  // Set role description
  roleDescriptionElement.textContent = data.roleDescription

  // Set additional info
  roleIdElement.textContent = `Role ID: ${data.roleId.slice(0, 8)}...`

  if (data.available !== undefined) {
    roleAvailableElement.textContent = `Available: ${data.available}`
  }
}

// Initialize the page
document.addEventListener("DOMContentLoaded", () => {
  loadRoleData()
})
