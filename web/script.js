const SIZE = 26;

const options = [
"Fox",
"Marth",
"Puff",
"Falco",
"Sheik",
"Falcon",
"Peach",
"Icies",
"Yoshi",
"Pikachu",
"Luigi",
"Samus",
"DK",
"Doc",
"Ganon",
"Link",
"Yink",
"Mario",
"G&W",
"Mewtwo",
"Roy",
"Zelda",
"Ness",
"Pichu",
"Kirby",
"Bowser"
];

const ratios = [
" ",
"10:90",
"15:85",
"20:80",
"25:75",
"30:70",
"35:65",
"40:60",
"45:55",
"50:50",
"55:45",
"60:40",
"65:35",
"70:30",
"75:25",
"80:20",
"85:15",
"90:10"
];

const matchupGrid = [];

let updatingMirror = false;

const resetButton =
    document.getElementById("resetButton");


const gridElement =
document.getElementById("matchupGrid");

const characterSelect =
document.getElementById("characterSelect");

const exportButton =
document.getElementById("exportButton");

const exportModal =
document.getElementById("exportModal");

const closeModal =
document.getElementById("closeModal");

const saveButton =
document.getElementById("saveButton");

const chartTitle =
document.getElementById("chartTitle");

const selectedCharacterImage =
document.getElementById("selectedCharacterImage");

const tiersElement =
document.getElementById("tiers");

// ==========================================
// INITIALIZE MATCHUP DATA
// ==========================================

function initializeData() {

for (let row = 0; row < SIZE; row++) {

    matchupGrid[row] = [];

    for (let col = 0; col < SIZE; col++) {

        if (row === col) {
            matchupGrid[row][col] = "50:50";
        } else {
            matchupGrid[row][col] = " ";
        }
    }
}

}

// ==========================================
// IMAGE PATH
// ==========================================

function imagePath(index) {

return `./images/option${index + 1}.png`;

}

// ==========================================
// RATIO COLOR
// ==========================================

function ratioColor(value) {

value = Math.max(10, Math.min(90, value));

const position = (value - 10) / 80;

let red;
let green;

// Red -> Yellow
if (position < 0.5) {

    const t = position / 0.5;

    red = 255;
    green = Math.floor(255 * t);

}

// Yellow -> Green
else {

    const t = (position - 0.5) / 0.5;

    red = Math.floor(255 * (1 - t));
    green = 255;
}

return `rgb(${red}, ${green}, 0)`;

}

// ==========================================
// UPDATE CELL COLOR
// ==========================================

function updateCellColor(select) {

const selected = select.value;

if (!selected || selected === " ") {

    select.style.backgroundColor = "white";
    select.style.color = "black";

    return;
}

const first =
    parseInt(selected.split(":")[0], 10);

select.style.backgroundColor =
    ratioColor(first);

select.style.color = "black";

}

// ==========================================
// MIRROR RATIO
// ==========================================

function mirrorRatio(ratio) {

if (!ratio || ratio === " ") {
    return " ";
}

const parts = ratio.split(":");

const first =
    parseInt(parts[0], 10);

const second =
    parseInt(parts[1], 10);

return `${second}:${first}`;

}

// ==========================================
// UPDATE MIRRORED CELL
// ==========================================

function updateMirror(row, col) {

// Diagonal cells mirror themselves.
if (row === col) {
    return;
}

const selected =
    matchupGrid[row][col];

if (!selected) {
    return;
}

const mirroredRatio =
    mirrorRatio(selected);

const mirrorRow = col;
const mirrorCol = row;

updatingMirror = true;

matchupGrid[mirrorRow][mirrorCol] =
    mirroredRatio;

const mirrorSelect =
    document.querySelector(
        `[data-row="${mirrorRow}"][data-col="${mirrorCol}"]`
    );

if (mirrorSelect) {

    mirrorSelect.value =
        mirroredRatio;

    updateCellColor(mirrorSelect);
}

updatingMirror = false;

}

// ==========================================
// CREATE RATIO SELECT
// ==========================================

function createRatioSelect(row, col) {

const select =
    document.createElement("select");

select.className = "matchup-cell";

select.dataset.row = row;
select.dataset.col = col;

// Add every possible ratio.
for (const ratio of ratios) {

    const option =
        document.createElement("option");

    option.value = ratio;
    option.textContent = ratio;

    select.appendChild(option);
}

// Set starting value.
select.value =
    matchupGrid[row][col];

// Handle changes.
select.addEventListener(
    "change",
    () => {

        matchupGrid[row][col] =
        select.value;

        updateCellColor(select);

        saveProgress();


        if (!updatingMirror) {
            updateMirror(row, col);
        }
    }
);

updateCellColor(select);

return select;

}

// ==========================================
// CREATE CHARACTER IMAGE
// ==========================================

function createCharacterImage(
index,
className = ""
) {

const image =
    document.createElement("img");

image.src =
    imagePath(index);

image.alt =
    options[index];

image.className =
    className;

return image;

}

// ==========================================
// BUILD MAIN GRID
// ==========================================

function buildGrid() {

gridElement.innerHTML = "";

// ======================================
// TOP-LEFT CORNER
// ======================================

const corner =
    document.createElement("div");

corner.className =
    "grid-corner";

gridElement.appendChild(corner);

// ======================================
// COLUMN LABELS
// ======================================

for (let col = 0; col < SIZE; col++) {

    const header =
        document.createElement("div");

    header.className =
        "character-header";

    header.title =
        options[col];

    header.appendChild(
        createCharacterImage(col)
    );

    gridElement.appendChild(header);
}

// ======================================
// ROWS
// ======================================

for (let row = 0; row < SIZE; row++) {

    // Row character
    const label =
        document.createElement("div");

    label.className =
        "character-label";

    label.title =
        options[row];

    label.appendChild(
        createCharacterImage(row)
    );

    gridElement.appendChild(label);

    // Row cells
    for (let col = 0; col < SIZE; col++) {

        const cell =
            createRatioSelect(row, col);

        gridElement.appendChild(cell);
    }
}

}

// ==========================================
// CHARACTER SELECTOR
// ==========================================

function buildCharacterSelector() {

characterSelect.innerHTML = "";

options.forEach(
    (character, index) => {

        const option =
            document.createElement("option");

        option.value = index;
        option.textContent = character;

        characterSelect.appendChild(option);
    }
);

}

// ==========================================
// BUILD EXPORT WINDOW
// ==========================================

function showMatchupWindow(selectedOption) {

const character =
    options[selectedOption];

// ======================================
// TITLE
// ======================================

chartTitle.value =
    `${character} Match Up Chart`;

// ======================================
// SELECTED CHARACTER
// ======================================

selectedCharacterImage.src =
    imagePath(selectedOption);

selectedCharacterImage.alt =
    character;

// ======================================
// CLEAR OLD TIERS
// ======================================

tiersElement.innerHTML = "";

// ======================================
// BUILD TIERS
// ======================================

for (
    let ratio = 90;
    ratio >= 10;
    ratio -= 5
) {

    let ratioUsed = false;

    const tier =
        document.createElement("div");

    tier.className =
        "tier";

    // ==================================
    // RATIO FIELD
    // ==================================

    const ratioField =
        document.createElement("input");

    ratioField.type = "text";

    ratioField.className =
        "tier-ratio";

    ratioField.value =
        `${ratio}:${100 - ratio}`;

    ratioField.style.backgroundColor =
        ratioColor(ratio);

    tier.appendChild(ratioField);

    // ==================================
    // FIND OPPONENTS
    // ==================================

    for (
        let other = 0;
        other < SIZE;
        other++
    ) {

        // Don't include the selected
        // character against itself.
        if (other === selectedOption) {
            continue;
        }

        const value =
            matchupGrid[selectedOption][other];

        if (
            value ===
            `${ratio}:${100 - ratio}`
        ) {

            ratioUsed = true;

            const opponent =
                document.createElement("div");

            opponent.className =
                "opponent";

            opponent.title =
                options[other];

            opponent.appendChild(
                createCharacterImage(other)
            );

            tier.appendChild(opponent);
        }
    }

    // ==================================
    // ONLY SHOW USED TIERS
    // ==================================

    if (ratioUsed) {
        tiersElement.appendChild(tier);
    }
}

// ======================================
// SHOW MODAL
// ======================================

exportModal.classList.remove("hidden");

exportModal.setAttribute(
    "aria-hidden",
    "false"
);

}

// ==========================================
// CLOSE EXPORT WINDOW
// ==========================================

function closeExportWindow() {

exportModal.classList.add("hidden");

exportModal.setAttribute(
    "aria-hidden",
    "true"
);

}

// ==========================================
// SAVE PANEL AS PNG
// ==========================================

async function savePanelAsPNG() {

const title =
    chartTitle.value ||
    "Melee Matchup Chart";

const selectedCharacter =
    characterSelect.selectedIndex;

// ======================================
// FIND USED TIERS
// ======================================

const usedTiers = [];

for (
    let ratio = 90;
    ratio >= 10;
    ratio -= 5
) {

    const opponents = [];

    for (
        let other = 0;
        other < SIZE;
        other++
    ) {

        if (other === selectedCharacter) {
            continue;
        }

        const value =
            matchupGrid[selectedCharacter][other];

        if (
            value ===
            `${ratio}:${100 - ratio}`
        ) {

            opponents.push(other);
        }
    }

    if (opponents.length > 0) {

        usedTiers.push({
            ratio: ratio,
            opponents: opponents
        });
    }
}

// ======================================
// LOAD ALL IMAGES
// ======================================

const imagePromises = [];

imagePromises.push(
    loadImage(
        imagePath(selectedCharacter)
    )
);

for (const tier of usedTiers) {

    for (const opponent of tier.opponents) {

        imagePromises.push(
            loadImage(
                imagePath(opponent)
            )
        );
    }
}

try {

    const loadedImages =
        await Promise.all(imagePromises);

    const characterImage =
        loadedImages.shift();

    // ==================================
    // CANVAS SIZE
    // ==================================

    const width = 1000;

    const titleHeight = 75;

    const characterHeight = 120;

    const tierHeight = 70;

    const height =
        titleHeight +
        characterHeight +
        (usedTiers.length * tierHeight) +
        30;

    // ==================================
    // CREATE CANVAS
    // ==================================

    const canvas =
        document.createElement("canvas");

    canvas.width = width;
    canvas.height = height;

    const ctx =
        canvas.getContext("2d");

    // ==================================
    // WHITE BACKGROUND
    // ==================================

    ctx.fillStyle = "white";

    ctx.fillRect(
        0,
        0,
        width,
        height
    );

    // ==================================
    // TITLE
    // ==================================

    ctx.fillStyle = "black";

    ctx.font =
        "bold 30px Arial";

    ctx.textAlign = "center";

    ctx.textBaseline = "middle";

    ctx.fillText(
        title,
        width / 2,
        38
    );

    // ==================================
    // SELECTED CHARACTER
    // ==================================

    drawContainedImage(
        ctx,
        characterImage,
        width / 2 - 50,
        titleHeight,
        100,
        100
    );

    // ==================================
    // DRAW TIERS
    // ==================================

    let y =
        titleHeight +
        characterHeight;

    let imageIndex = 0;

    for (const tierData of usedTiers) {

        const ratio =
            tierData.ratio;

        // ==============================
        // TIER BACKGROUND
        // ==============================

        ctx.fillStyle = "white";

        ctx.fillRect(
            20,
            y,
            width - 40,
            tierHeight
        );

        // ==============================
        // TIER BORDER
        // ==============================

        ctx.strokeStyle =
            "#808080";

        ctx.lineWidth = 2;

        ctx.strokeRect(
            20,
            y,
            width - 40,
            tierHeight
        );

        // ==============================
        // RATIO BOX
        // ==============================

        ctx.fillStyle =
            ratioColor(ratio);

        ctx.fillRect(
            35,
            y + 17,
            80,
            35
        );

        ctx.strokeStyle =
            "#444444";

        ctx.lineWidth = 1;

        ctx.strokeRect(
            35,
            y + 17,
            80,
            35
        );

        // ==============================
        // RATIO TEXT
        // ==============================

        ctx.fillStyle = "black";

        ctx.font =
            "bold 14px Arial";

        ctx.textAlign = "center";

        ctx.textBaseline = "middle";

        ctx.fillText(
            `${ratio}:${100 - ratio}`,
            75,
            y + 34
        );

        // ==============================
        // OPPONENT IMAGES
        // ==============================

        let x = 135;

        for (
            const opponent
            of tierData.opponents
        ) {

            const opponentImage =
                loadedImages[imageIndex];

            imageIndex++;

            drawContainedImage(
                ctx,
                opponentImage,
                x,
                y + 8,
                48,
                48
            );

            x += 58;
        }

        y += tierHeight;
    }

    // ==================================
    // DOWNLOAD PNG
    // ==================================

    const safeName =
        title
            .replace(/[<>:"/\\|?*]+/g, "")
            .trim()
            .replace(/\s+/g, "_") ||
        "matchup_chart";

    canvas.toBlob(
        (blob) => {

            if (!blob) {

                alert(
                    "Could not create PNG."
                );

                return;
            }

            const url =
                URL.createObjectURL(blob);

            const link =
                document.createElement("a");

            link.href = url;

            link.download =
                `${safeName}.png`;

            document.body.appendChild(link);

            link.click();

            link.remove();

            URL.revokeObjectURL(url);
        },
        "image/png"
    );

}

catch (error) {

    console.error(error);

    alert(
        "Could not create the PNG. " +
        "Make sure the character images exist."
    );
}


}

// ==========================================
// RESET PROGRESS
// ==========================================

function resetProgress() {

    const confirmed =
        confirm(
            "Are you sure you want to reset all matchup data?"
        );

    if (!confirmed) {
        return;
    }

    // Reset the matchup grid.
    initializeData();

    // Remove saved progress.
    clearProgress();

    // Rebuild the grid so the dropdowns
    // show the newly reset values.
    buildGrid();
}

// ==========================================
// LOAD IMAGE
// ==========================================

function loadImage(src) {

return new Promise(
    (resolve, reject) => {

        const image =
            new Image();

        image.onload = () => {
            resolve(image);
        };

        image.onerror = () => {

            reject(
                new Error(
                    `Could not load ${src}`
                )
            );
        };

        image.src = src;
    }
);

}

// ==========================================
// DRAW IMAGE WHILE PRESERVING ASPECT RATIO
// ==========================================

function drawContainedImage(
ctx,
image,
x,
y,
width,
height
) {

const imageRatio =
    image.width / image.height;

const boxRatio =
    width / height;

let drawWidth;
let drawHeight;

if (imageRatio > boxRatio) {

    drawWidth = width;

    drawHeight =
        width / imageRatio;

}

else {

    drawHeight = height;

    drawWidth =
        height * imageRatio;
}

const drawX =
    x + (width - drawWidth) / 2;

const drawY =
    y + (height - drawHeight) / 2;

ctx.drawImage(
    image,
    drawX,
    drawY,
    drawWidth,
    drawHeight
);

}

// ==========================================
// BUTTON EVENTS
// ==========================================

exportButton.addEventListener(
"click",
() => {

    showMatchupWindow(
        characterSelect.selectedIndex
    );
}

);

closeModal.addEventListener(
"click",
closeExportWindow
);

saveButton.addEventListener(
"click",
savePanelAsPNG
);

// Close when clicking outside modal.
exportModal.addEventListener(
"click",
(event) => {

    if (event.target === exportModal) {
        closeExportWindow();
    }
}

);

resetButton.addEventListener(
    "click",
    resetProgress
);


// Close with Escape.
document.addEventListener(
"keydown",
(event) => {

    if (
        event.key === "Escape" &&
        !exportModal.classList.contains("hidden")
    ) {

        closeExportWindow();
    }
}

);

// ==========================================
// SAVE / LOAD PROGRESS
// ==========================================

const SAVE_KEY = "meleeMatchupProgress";

function saveProgress() {

    const saveData = {
        matchupGrid: matchupGrid,
        selectedCharacter: characterSelect.selectedIndex
    };

    localStorage.setItem(
        SAVE_KEY,
        JSON.stringify(saveData)
    );
}

function loadProgress() {

    const savedData =
        localStorage.getItem(SAVE_KEY);

    if (!savedData) {
        return false;
    }

    try {

        const data =
            JSON.parse(savedData);

        // Make sure the saved grid has
        // the correct dimensions.
        if (
            !data.matchupGrid ||
            data.matchupGrid.length !== SIZE
        ) {
            return false;
        }

        // Restore matchup grid.
        for (let row = 0; row < SIZE; row++) {

            if (
                !data.matchupGrid[row] ||
                data.matchupGrid[row].length !== SIZE
            ) {
                return false;
            }

            matchupGrid[row] =
                data.matchupGrid[row];
        }

        // Restore selected character.
        if (
            typeof data.selectedCharacter === "number" &&
            data.selectedCharacter >= 0 &&
            data.selectedCharacter < SIZE
        ) {

            characterSelect.selectedIndex =
                data.selectedCharacter;
        }

        return true;

    }

    catch (error) {

        console.error(
            "Could not load saved progress:",
            error
        );

        return false;
    }
}

function clearProgress() {

    localStorage.removeItem(SAVE_KEY);
}


// ==========================================
// START APPLICATION
// ==========================================

initializeData();

buildCharacterSelector();

const hasSavedProgress =
    loadProgress();

buildGrid();
