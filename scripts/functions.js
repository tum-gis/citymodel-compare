// Constant precision
const PRECISION = 0.010;
const DATETIME_TOLERANCE = 60000; // 1 minute

function fuzzyEquals(a, b) {
    // Convert to strings
    as = a.toString().trim();
    bs = b.toString().trim();

    const numRegex = /(-?\d+)(\.\d+)?(e(-?\d+))?/;
    // Regex for strings such as "2016-11-22T00:00+01:00[Europe/Berlin]"
    const dateRegex = /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}[+-]\d{2}:\d{2}\[[A-Za-z/]+\]/g;

    const numMatchA = as.match(numRegex);
    const numMatchB = bs.match(numRegex);
    const dateMatchA = as.match(dateRegex);
    const dateMatchB = bs.match(dateRegex);

    if (numMatchA && numMatchB) {
        const valueA = parseFloat(numMatchA[0]);
        const valueB = parseFloat(numMatchB[0]);
        return Math.abs(valueA - valueB) < PRECISION;
    }

    if (dateMatchA && dateMatchB) {
        const valueA = new Date(dateMatchA[0]);
        const valueB = new Date(dateMatchB[0]);
        return Math.abs(valueA - valueB) < DATETIME_TOLERANCE;
    }

    return a === b;
}

// The inputs are in format "minx,miny;maxx,maxy"
function spatialEquals(a, b) {
    const aParts = a.split(';');
    const bParts = b.split(';');
    if (aParts.length !== 2 || bParts.length !== 2) {
        return false;
    }

    const aMin = aParts[0].split(',').map(parseFloat);
    const aMax = aParts[1].split(',').map(parseFloat);
    const bMin = bParts[0].split(',').map(parseFloat);
    const bMax = bParts[1].split(',').map(parseFloat);

    return fuzzyEquals(aMin[0], bMin[0]) &&
        fuzzyEquals(aMin[1], bMin[1]) &&
        fuzzyEquals(aMax[0], bMax[0]) &&
        fuzzyEquals(aMax[1], bMax[1]);
}