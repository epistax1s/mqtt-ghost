# Website

This site is built with [Docusaurus](https://docusaurus.io/), a static site generator for documentation.

### Prerequisites

* `Node.js` (16.x+)
* `npm`

### Installation

```bash
npm install
```

Installs dependencies from package.json.

### Local Development

```
npm start
```

Starts a local server at http://localhost:3000/mqtt-ghost/ with live reloading.

### Build

```
npm run build
```

Generates static files in the `build/` directory. The deployment to GitHub Pages is handled by a GitHub Action located in
`.github/workflows/docs.yaml`.

Preview the build locally:

```bash
npm run serve
```