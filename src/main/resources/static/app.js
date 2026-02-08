// API Base URL
const API_BASE = "/api";

// State
let heroes = [];
let currentDraft = null;
let selectedHeroId = null;

// DOM Elements
const heroGrid = document.getElementById("heroGrid");
const searchInput = document.getElementById("searchHero");
const attributeFilter = document.getElementById("filterAttribute");
const syncButton = document.getElementById("syncHeroes");
const startDraftButton = document.getElementById("startDraft");
const currentTurnDiv = document.getElementById("currentTurn");
const phaseText = document.getElementById("phaseText");

// Initialize
document.addEventListener("DOMContentLoaded", () => {
  loadHeroes();
  setupEventListeners();
});

function setupEventListeners() {
  syncButton.addEventListener("click", syncHeroes);
  startDraftButton.addEventListener("click", startNewDraft);
  searchInput.addEventListener("input", filterHeroes);
  attributeFilter.addEventListener("change", filterHeroes);
}

// API Calls
async function loadHeroes() {
  try {
    const response = await fetch(`${API_BASE}/heroes`);
    if (!response.ok) throw new Error("Failed to load heroes");
    heroes = await response.json();
    renderHeroGrid();
  } catch (error) {
    heroGrid.innerHTML =
      '<p class="error">Failed to load heroes. Please sync from API first.</p>';
    console.error("Error loading heroes:", error);
  }
}

async function syncHeroes() {
  syncButton.disabled = true;
  syncButton.textContent = "Syncing...";

  try {
    const response = await fetch(`${API_BASE}/heroes/sync`, {
      method: "POST",
    });

    if (!response.ok) throw new Error("Sync failed");

    const message = await response.text();
    alert(message);
    await loadHeroes();
  } catch (error) {
    alert("Failed to sync heroes: " + error.message);
    console.error("Error syncing heroes:", error);
  } finally {
    syncButton.disabled = false;
    syncButton.textContent = "Sync Heroes from API";
  }
}

async function startNewDraft() {
  try {
    const response = await fetch(`${API_BASE}/draft/start`, {
      method: "POST",
    });

    if (!response.ok) throw new Error("Failed to start draft");

    currentDraft = await response.json();
    updateDraftUI();
    updateHeroCardStatuses(); // Update hero card statuses instead of full re-render
  } catch (error) {
    alert("Failed to start draft: " + error.message);
    console.error("Error starting draft:", error);
  }
}

async function pickHero(heroId) {
  if (!currentDraft) {
    alert("Please start a draft first!");
    return;
  }

  try {
    const response = await fetch(
      `${API_BASE}/draft/${currentDraft.id}/pick/${heroId}`,
      {
        method: "POST",
      },
    );

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Failed to pick hero");
    }

    currentDraft = await response.json();
    updateDraftUI();
    updateHeroCardStatuses(); // Only update statuses, not full re-render
  } catch (error) {
    alert("Failed to pick hero: " + error.message);
    console.error("Error picking hero:", error);
  }
}

async function banHero(heroId) {
  if (!currentDraft) {
    alert("Please start a draft first!");
    return;
  }

  try {
    const response = await fetch(
      `${API_BASE}/draft/${currentDraft.id}/ban/${heroId}`,
      {
        method: "POST",
      },
    );

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Failed to ban hero");
    }

    currentDraft = await response.json();
    updateDraftUI();
    updateHeroCardStatuses(); // Only update statuses, not full re-render
  } catch (error) {
    alert("Failed to ban hero: " + error.message);
    console.error("Error banning hero:", error);
  }
}

// UI Rendering
function renderHeroGrid() {
  const searchTerm = searchInput.value.toLowerCase();
  const selectedAttribute = attributeFilter.value;

  const filteredHeroes = heroes.filter((hero) => {
    const matchesSearch = hero.name.toLowerCase().includes(searchTerm);
    const matchesAttribute =
      !selectedAttribute || hero.primaryAttribute === selectedAttribute;
    return matchesSearch && matchesAttribute;
  });

  if (filteredHeroes.length === 0) {
    heroGrid.innerHTML = '<p class="loading">No heroes found</p>';
    return;
  }

  // Group heroes by attribute
  const attributes = {
    STRENGTH: [],
    AGILITY: [],
    INTELLIGENCE: [],
    UNIVERSAL: [],
  };

  filteredHeroes.forEach((hero) => {
    if (attributes[hero.primaryAttribute]) {
      attributes[hero.primaryAttribute].push(hero);
    }
  });

  // Helper to generate hero card HTML
  const escapeHtml = (str) =>
    String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  // Helper to generate hero card HTML
  const generateHeroCard = (hero) => {
    const isPicked = isHeroPicked(hero.id);
    const isBanned = isHeroBanned(hero.id);
    const statusClass = isPicked ? "picked" : isBanned ? "banned" : "";
    const safeName = escapeHtml(hero.name ?? "");
    return `
            <div class="hero-card ${statusClass}" data-hero-id="${hero.id}" onclick="handleHeroClick(${hero.id})">
                <img src="${hero.imageUrl}" alt="${safeName}" onerror="this.src='data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%22100%22 height=%22120%22%3E%3Crect width=%22100%22 height=%22120%22 fill=%22%23333%22/%3E%3Ctext x=%2250%%22 y=%2250%%22 text-anchor=%22middle%22 fill=%22%23666%22 font-size=%2216%22%3ENo Image%3C/text%3E%3C/svg%3E'">
                <div class="hero-info">
                    <div class="hero-name">${safeName}</div>
                </div>
            </div>
        `;
  };

  // Build grid HTML with columns
  const columnsHtml = Object.entries(attributes)
    .map(([attr, heroList]) => {
      // Don't render empty columns if we are filtering by specific attribute
      if (
        selectedAttribute &&
        attr !== selectedAttribute &&
        heroList.length === 0
      ) {
        return "";
      }

      const icon = getAttributeIcon(attr);
      const heroCardsHtml = heroList.map(generateHeroCard).join("");

      return `
        <div class="attribute-column ${attr.toLowerCase()}">
            <h3>${icon} ${attr}</h3>
            <div class="column-heroes">
                ${heroCardsHtml}
            </div>
        </div>
      `;
    })
    .join("");

  heroGrid.innerHTML = columnsHtml;
}

function getAttributeIcon(attr) {
  switch (attr) {
    case "STRENGTH":
      return "💪";
    case "AGILITY":
      return "🦶";
    case "INTELLIGENCE":
      return "🧠";
    case "UNIVERSAL":
      return "✨";
    default:
      return "";
  }
}

// Efficient update function that only changes hero card classes
function updateHeroCardStatuses() {
  const heroCards = document.querySelectorAll(".hero-card");
  heroCards.forEach((card) => {
    const heroId = parseInt(card.getAttribute("data-hero-id"));
    const isPicked = isHeroPicked(heroId);
    const isBanned = isHeroBanned(heroId);

    // Remove all status classes
    card.classList.remove("picked", "banned");

    // Add appropriate status class
    if (isPicked) {
      card.classList.add("picked");
    } else if (isBanned) {
      card.classList.add("banned");
    }
  });
}

function handleHeroClick(heroId) {
  if (!currentDraft) {
    alert("Please start a draft first!");
    return;
  }

  if (isHeroPicked(heroId) || isHeroBanned(heroId)) {
    return;
  }

  const action = currentDraft.pickPhase ? "pick" : "ban";
  const team = currentDraft.radiantTurn ? "Radiant" : "Dire";

  if (confirm(`${action.toUpperCase()} ${getHeroName(heroId)} for ${team}?`)) {
    if (action === "pick") {
      pickHero(heroId);
    } else {
      banHero(heroId);
    }
  }
}

function updateDraftUI() {
  if (!currentDraft) return;

  // Update team panels
  updateTeamPanel("radiantPicks", currentDraft.radiantPicks);
  updateTeamPanel("radiantBans", currentDraft.radiantBans);
  updateTeamPanel("direPicks", currentDraft.direPicks);
  updateTeamPanel("direBans", currentDraft.direBans);

  // Update turn indicator
  const team = currentDraft.radiantTurn ? "Radiant" : "Dire";
  const phase = currentDraft.pickPhase ? "PICK" : "BAN";
  const turnClass = currentDraft.radiantTurn ? "radiant-turn" : "dire-turn";

  currentTurnDiv.className = `turn-indicator ${turnClass}`;
  currentTurnDiv.innerHTML = `<p><strong>${team}'s Turn</strong></p><p>Phase: ${phase}</p>`;
  phaseText.textContent = phase;

  if (currentDraft.complete) {
    currentTurnDiv.innerHTML = "<p><strong>Draft Complete!</strong></p>";
    phaseText.textContent = "COMPLETE";
  }
}

function updateTeamPanel(elementId, heroList) {
  const container = document.getElementById(elementId);
  const slots = container.querySelectorAll(".hero-slot");

  heroList.forEach((hero, index) => {
    if (slots[index]) {
      slots[index].classList.remove("empty");
      slots[index].innerHTML = `
                <img src="${hero.imageUrl}" alt="${hero.name}">
                <div class="hero-name">${hero.name}</div>
            `;
    }
  });
}

function isHeroPicked(heroId) {
  if (!currentDraft) return false;
  return [...currentDraft.radiantPicks, ...currentDraft.direPicks].some(
    (hero) => hero.id === heroId,
  );
}

function isHeroBanned(heroId) {
  if (!currentDraft) return false;
  return [...currentDraft.radiantBans, ...currentDraft.direBans].some(
    (hero) => hero.id === heroId,
  );
}

function getHeroName(heroId) {
  const hero = heroes.find((h) => h.id === heroId);
  return hero ? hero.name : "Unknown";
}

function filterHeroes() {
  renderHeroGrid();
}
