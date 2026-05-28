#!/usr/bin/env bash
#
# Builds Android (debug APK) and iOS (debug simulator .app) in parallel.
#
# Usage:
#   ./build-all.sh              # builds both in parallel
#   ./build-all.sh android      # builds only Android
#   ./build-all.sh ios          # builds only iOS

set -u
set -o pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="${SCRIPT_DIR}/build/logs"
mkdir -p "${LOG_DIR}"

ANDROID_LOG="${LOG_DIR}/android-debug.log"
IOS_LOG="${LOG_DIR}/ios-debug.log"
IOS_DERIVED_DATA="${SCRIPT_DIR}/build/ios-derived-data"

GREEN=$'\033[0;32m'
RED=$'\033[0;31m'
YELLOW=$'\033[0;33m'
RESET=$'\033[0m'

build_android() {
  echo "${YELLOW}[android]${RESET} building :app:assembleDebug ..."
  if (cd "${SCRIPT_DIR}" && ./gradlew :app:assembleDebug) >"${ANDROID_LOG}" 2>&1; then
    local apk="${SCRIPT_DIR}/app/build/outputs/apk/debug/app-debug.apk"
    echo "${GREEN}[android] OK${RESET} → ${apk}"
    return 0
  else
    echo "${RED}[android] FAILED${RESET} (see ${ANDROID_LOG})"
    return 1
  fi
}

build_ios() {
  echo "${YELLOW}[ios]${RESET} building iosApp Debug (simulator) ..."
  if (cd "${SCRIPT_DIR}/iosApp" && \
      xcodebuild \
        -project iosApp.xcodeproj \
        -scheme iosApp \
        -configuration Debug \
        -destination 'generic/platform=iOS Simulator' \
        -derivedDataPath "${IOS_DERIVED_DATA}" \
        build \
        CODE_SIGNING_ALLOWED=NO) >"${IOS_LOG}" 2>&1; then
    local app="${IOS_DERIVED_DATA}/Build/Products/Debug-iphonesimulator/iosApp.app"
    echo "${GREEN}[ios] OK${RESET} → ${app}"
    return 0
  else
    echo "${RED}[ios] FAILED${RESET} (see ${IOS_LOG})"
    return 1
  fi
}

target="${1:-all}"
case "${target}" in
  android)
    build_android
    exit $?
    ;;
  ios)
    build_ios
    exit $?
    ;;
  all)
    build_android &
    android_pid=$!
    build_ios &
    ios_pid=$!

    wait "${android_pid}"; android_status=$?
    wait "${ios_pid}";     ios_status=$?

    echo
    echo "===== summary ====="
    if [[ ${android_status} -eq 0 ]]; then
      echo "${GREEN}android: OK${RESET}"
    else
      echo "${RED}android: FAILED (${ANDROID_LOG})${RESET}"
    fi
    if [[ ${ios_status} -eq 0 ]]; then
      echo "${GREEN}ios:     OK${RESET}"
    else
      echo "${RED}ios:     FAILED (${IOS_LOG})${RESET}"
    fi

    if [[ ${android_status} -ne 0 || ${ios_status} -ne 0 ]]; then
      exit 1
    fi
    ;;
  *)
    echo "Usage: $0 [android|ios|all]" >&2
    exit 2
    ;;
esac
