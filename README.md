# utils
Handy utilities that cover various areas 

## Running the security demos (`security` package)
- RSA key files are written to and read from `~/.utils-keys` (created owner-only); override with `-Dutils.keyDir=/path`.
  Run `KeyPairFileCreator` first, then `ASymetricKeyEncryptDecrypt`.
- `MainClass` (keystore demo) needs the keystore password in the `KEYSTORE_PASSWORD` environment variable.
- The `DBWrapper` test helper reads `DB_URL` (default `jdbc:mysql://localhost:3306/test`), `DB_USER` and `DB_PASSWORD` from the environment.
