from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED, ZipInfo
import hashlib
import shutil

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "delivery"
OUT.mkdir(exist_ok=True)
apk = ROOT / "app/build/outputs/apk/debug/app-debug.apk"
if not apk.is_file():
    raise SystemExit("Primero ejecuta gradlew :app:assembleDebug")
target_apk = OUT / "Moivo-debug.apk"
shutil.copy2(apk, target_apk)

roots = ["app", "gradle", "docs", "scripts", "verification"]
files = [ROOT / name for name in [
    "README.md", "NOTICE.md", ".gitignore", "settings.gradle", "build.gradle",
    "gradle.properties", "gradlew", "gradlew.bat", "local.properties.example"
]]
for folder in roots:
    for path in (ROOT / folder).rglob("*"):
        if not path.is_file():
            continue
        relative = path.relative_to(ROOT)
        if "build" in relative.parts or "__pycache__" in relative.parts:
            continue
        if path.name.startswith("emulator") or path.name == "screen-check.png":
            continue
        files.append(path)
archive = OUT / "Moivo-Android-proyecto.zip"
with ZipFile(archive, "w", ZIP_DEFLATED, compresslevel=9) as z:
    for path in sorted(set(files)):
        relative = path.relative_to(ROOT).as_posix()
        if path.name == "gradlew":
            info = ZipInfo("Moivo/" + relative)
            info.create_system = 3
            info.external_attr = 0o100755 << 16
            info.compress_type = ZIP_DEFLATED
            z.writestr(info, path.read_bytes())
        else:
            z.write(path, "Moivo/" + relative)
with ZipFile(archive) as z:
    assert z.testzip() is None, "Error de integridad ZIP"
    assert "Moivo/gradle/wrapper/gradle-wrapper.jar" in z.namelist()
    assert not any(n.endswith("/local.properties") for n in z.namelist())
    assert not any("/.tools/" in n or "/build/" in n for n in z.namelist())
manifest = []
for path in [target_apk, archive]:
    digest = hashlib.sha256(path.read_bytes()).hexdigest()
    manifest.append(f"{digest}  {path.name}")
    print(f"{path.name}: {path.stat().st_size:,} bytes")
(OUT / "SHA256SUMS.txt").write_text("\n".join(manifest) + "\n", encoding="utf-8")
print(f"Proyecto: {len(files)} archivos; ZIP verificado.")
