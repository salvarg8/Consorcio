#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if command -v rg >/dev/null 2>&1; then
  SEARCH_CMD="rg"
else
  SEARCH_CMD="grep"
fi


echo "[check] Verificando estructura base de paquetes"
required_dirs=(
  "src/main/java/com/consorcio/gestion/controller"
  "src/main/java/com/consorcio/gestion/service"
  "src/main/java/com/consorcio/gestion/repository"
  "src/main/java/com/consorcio/gestion/entity"
  "src/main/java/com/consorcio/gestion/dto"
  "src/main/java/com/consorcio/gestion/mapper"
  "src/main/java/com/consorcio/gestion/exception"
)

for dir in "${required_dirs[@]}"; do
  if [[ ! -d "$dir" ]]; then
    echo "ERROR: falta el directorio requerido $dir"
    exit 1
  fi
done

if [[ -d "src/main/java/org/example" ]]; then
  echo "ERROR: directorio legacy detectado: src/main/java/org/example"
  exit 1
fi

if [[ -d "src/main/webapp" ]]; then
  echo "ERROR: directorio legacy detectado: src/main/webapp"
  exit 1
fi

echo "[check] Buscando archivos placeholder (// DELETED)"
if [[ "$SEARCH_CMD" == "rg" ]]; then
  if rg -n --glob '*.java' '^\s*//\s*DELETED\s*$' src/main/java src/test/java; then
    echo "ERROR: se detectaron placeholders // DELETED"
    exit 1
  fi
else
  if grep -RnsE '^\s*//\s*DELETED\s*$' src/main/java src/test/java --include='*.java'; then
    echo "ERROR: se detectaron placeholders // DELETED"
    exit 1
  fi
fi

echo "[check] Validando sincronización README vs implementación (Soft Delete)"
soft_entities=(
  "Consorcio.java"
  "Administracion.java"
  "Usuario.java"
  "UnidadFuncional.java"
  "Amenity.java"
  "Infraccion.java"
  "ReservaAmenity.java"
  "PagoPendiente.java"
  "LiquidacionMensual.java"
  "LiquidacionUnidad.java"
)

for entity in "${soft_entities[@]}"; do
  file="src/main/java/com/consorcio/gestion/entity/${entity}"
  if [[ "$SEARCH_CMD" == "rg" ]]; then
    if ! rg -q '@SQLDelete|set(Activo|Activa|Habilitado)\(false\)' "$file"; then
      echo "ERROR: ${entity} no evidencia estrategia de soft delete"
      exit 1
    fi
  else
    if ! grep -Eq '@SQLDelete|set(Activo|Activa|Habilitado)\(false\)' "$file"; then
      echo "ERROR: ${entity} no evidencia estrategia de soft delete"
      exit 1
    fi
  fi
done

if [[ "$SEARCH_CMD" == "rg" ]]; then
  if ! rg -q 'interfaz \+ implementación|interface \+ implementation' README.md; then
    echo "ERROR: README no documenta la política de interfaz + implementación"
    exit 1
  fi
  if ! rg -q 'Dominio interno .*español|API pública .*inglés' README.md; then
    echo "ERROR: README no documenta estándar de naming ES/EN"
    exit 1
  fi
else
  if ! grep -Eq 'interfaz \+ implementación|interface \+ implementation' README.md; then
    echo "ERROR: README no documenta la política de interfaz + implementación"
    exit 1
  fi

  if ! grep -Eq 'Dominio interno .*español|API pública .*inglés' README.md; then
    echo "ERROR: README no documenta estándar de naming ES/EN"
    exit 1
  fi
fi

echo "[ok] Checklist de consistencia arquitectónica completada"
