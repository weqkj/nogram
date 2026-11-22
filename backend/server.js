// Nogram badges backend. Без зависимостей: node server.js
// ENV: ADMIN_TOKEN (длинный секрет владельца), PORT, DATA_FILE
const http = require("http");
const fs = require("fs");
const crypto = require("crypto");

const PORT = process.env.PORT || 8080;
const DATA_FILE = process.env.DATA_FILE || "./badges.json";
const ADMIN_TOKEN = process.env.ADMIN_TOKEN;
const OWNER_ID = "7876031245";
const BADGES = ["owner","donor","veteran","dev","plugin_author","tester","designer","translator","verified","star"];

if (!ADMIN_TOKEN || ADMIN_TOKEN.length < 24) { console.error("Задайте ADMIN_TOKEN (>=24 символов)"); process.exit(1); }

let db = {};
try { db = JSON.parse(fs.readFileSync(DATA_FILE, "utf8")); } catch {}
db[OWNER_ID] = Array.from(new Set([...(db[OWNER_ID] || []), "owner"])); // владелец всегда имеет значок
const save = () => fs.writeFileSync(DATA_FILE, JSON.stringify(db, null, 2));

const isAdmin = (req) => {
  const h = req.headers.authorization || "";
  const a = Buffer.from(h.replace(/^Bearer /, "")), b = Buffer.from(ADMIN_TOKEN);
  return a.length === b.length && crypto.timingSafeEqual(a, b);
};
const send = (res, code, obj) => { res.writeHead(code, { "content-type": "application/json" }); res.end(JSON.stringify(obj)); };
const readBody = (req) => new Promise((ok, no) => {
  let s = ""; req.on("data", (c) => { s += c; if (s.length > 4096) { req.destroy(); no(); } });
  req.on("end", () => { try { ok(JSON.parse(s)); } catch { no(); } });
});

http.createServer(async (req, res) => {
  const m = req.url.match(/^\/badges\/(\d{1,15})$/);
  if (req.method === "GET" && m) return send(res, 200, { badges: db[m[1]] || [] });

  const a = req.url.match(/^\/admin\/(grant|revoke)$/);
  if (req.method === "POST" && a) {
    if (!isAdmin(req)) return send(res, 403, { error: "forbidden" });
    let body; try { body = await readBody(req); } catch { return send(res, 400, { error: "bad json" }); }
    const uid = String(body.userId || ""), badge = body.badge;
    if (!/^\d{1,15}$/.test(uid) || !BADGES.includes(badge)) return send(res, 400, { error: "bad params" });
    const cur = new Set(db[uid] || []);
    if (a[1] === "grant") cur.add(badge); else if (!(uid === OWNER_ID && badge === "owner")) cur.delete(badge);
    db[uid] = [...cur]; save();
    return send(res, 200, { ok: true, badges: db[uid] });
  }
  send(res, 404, { error: "not found" });
}).listen(PORT, () => console.log("badges backend on :" + PORT));
