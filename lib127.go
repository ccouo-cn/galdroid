package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net"
	"net/http"
	"os"
	"strings"
)

const (
	hexPath = "/storage/emulated/0/Android/galdroid/v1.c"
	key     = "mykey123"
)

func xorDecrypt(data []byte, key string) []byte {
	result := make([]byte, len(data))
	for i := range data {
		result[i] = data[i] ^ key[i%len(key)]
	}
	return result
}

func parseHexArray(hexStr string) ([]byte, error) {
	var bytes []byte
	s := strings.ReplaceAll(hexStr, "0x", "")
	s = strings.ReplaceAll(s, ",", "")
	s = strings.ReplaceAll(s, " ", "")
	s = strings.ReplaceAll(s, "\n", "")
	if len(s)%2 != 0 {
		return nil, fmt.Errorf("HEX 长度非法")
	}
	for i := 0; i < len(s); i += 2 {
		var b byte
		fmt.Sscanf(s[i:i+2], "%02x", &b)
		bytes = append(bytes, b)
	}
	return bytes, nil
}

func getFreePort() (int, error) {
	l, err := net.Listen("tcp", ":0")
	if err != nil {
		return 0, err
	}
	defer l.Close()
	return l.Addr().(*net.TCPAddr).Port, nil
}

func main() {
	hexContent, err := os.ReadFile(hexPath)
	if err != nil {
		log.Fatal("读取 v1.c 失败:", err)
	}
	encrypted, err := parseHexArray(string(hexContent))
	if err != nil {
		log.Fatal("HEX 解析失败:", err)
	}
	htmlBytes := xorDecrypt(encrypted, key)
	port, err := getFreePort()
	if err != nil {
		log.Fatal("无法获取端口:", err)
	}
	serverAddr := fmt.Sprintf("127.0.0.1:%d", port)
	resp := map[string]string{
		"status": "ok",
		"server": "http://" + serverAddr + "/v1.html",
	}
	json.NewEncoder(os.Stdout).Encode(resp)
	http.HandleFunc("/v1.json", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json; charset=utf-8")
		json.NewEncoder(w).Encode(resp)
	})
	http.HandleFunc("/v1.html", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "text/html; charset=utf-8")
		w.Write(htmlBytes)
	})
	log.Fatal(http.ListenAndServe(serverAddr, nil))
}
