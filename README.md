# Muffle Socket Server
<details><summary>Config file</summary>
<p>

```properties
# === GENERAL ===
general.errorPage=sites/ErrorTemplate.html
general.verifiedPage=sites/UserVerified.html
general.tokenExpiredPage=sites/TokenExpired.html
# === DB ===
db.url=jdbc:mysql://localhost:3306/muffle?serverTimezone=UTC
db.user=
db.password=
# === JWT ===
jwt.key=
# === MAIL ===
mail.host=mail.scharez.at
mail.port=587
mail.user=no-reply@scharez.at
mail.password=
# === GENERAL ===
server.host=http://0.0.0.0
server.port=8080
server.route=/rest
```

</p>
</details>

## Requests (Socket)
<details><summary>Default-Request</summary>
<p>

```JSON
{"type":  ""}
```

</p>
</details>
<details><summary>Auth-Request</summary>
<p>

```JSON
{"type":  "auth", "token":  ""}
```

</p>
</details>

<details><summary>Playlist-Requests</summary>
<br><br> Add Song
<p>

```JSON
{"type":  "addSongToPlaylist", "songID":  0, "playlistID":  0, "mufflerID":  0}
```

</p>
Remove Song
<p>

```JSON
{"type":  "removeSongFromPlaylist", "playlistID":  0, "songID":  0}
```

</p>
Invite Muffler
<p>

```JSON
{"type":  ""}
```

</p>
</details>
<details><summary>Session-Requests</summary>
<br><br> Add Song
<p>

```JSON
{"type":  "addSongToPlaylist", "songID":  0, "playlistID":  0, "mufflerID":  0}
```

</p>
Remove Song
<p>

```JSON
{"type":  "removeSongFromPlaylist", "playlistID":  0, "songID":  0}
```

</p>
Invite Muffler
<p>

```JSON
{"type":  ""}
```

</p>
</details>