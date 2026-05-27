#!/system/bin/sh

LIB="/data/user/0/com.galgame.android/files/lib127"
LOG_DIR="/storage/emulated/0/Android/data/com.galgame.android"
LOG="$LOG_DIR/log.txt"

mkdir -p "$LOG_DIR"


chmod 777 "$LIB"


"$LIB" > "$LOG" 2>&1

