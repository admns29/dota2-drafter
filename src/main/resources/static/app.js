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

    if (!response.ok) throw new Error("Failed to pick hero");

    currentDraft = await response.json();
    updateDraftUI();
    renderHeroGrid();
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

    if (!response.ok) throw new Error("Failed to ban hero");

    currentDraft = await response.json();
    updateDraftUI();
    renderHeroGrid();
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

  heroGrid.innerHTML = filteredHeroes
    .map((hero) => {
      const isPicked = isHeroPicked(hero.id);
      const isBanned = isHeroBanned(hero.id);
      const statusClass = isPicked ? "picked" : isBanned ? "banned" : "";

      return `
            <div class="hero-card ${statusClass}" onclick="handleHeroClick(${hero.id})">
                <img src="${hero.imageUrl}" alt="${hero.name}" onerror="this.src='data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%22100%22 height=%22120%22><rect width=%22100%22 height=%22120%22 fill=%22%23333%22/><text x=%2250%%22 y=%2250%%22 text-anchor=%22middle%22 fill=%22%23666%22 font-size=%2216%22>No Image</text></svg>'">
                <div class="hero-info">
                    <div class="hero-name">${hero.name}</div>
                    <div class="hero-attr ${hero.primaryAttribute}">${hero.primaryAttribute}</div>
                </div>
            </div>
        `;
    })
    .join("");
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
