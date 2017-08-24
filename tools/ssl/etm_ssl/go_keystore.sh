# READ https://www.go.cd/2014/06/05/using-go-cd-with-custom-certificates.html
#openssl rsa -des3 -in stlerappdev.perficient.com.key -out stlerappdev-go.perficient.com.key
openssl pkcs12 -export -out keystore-go.p12 -inkey stlerappdev-go.perficient.com.key -in stlerappdev.perficient.com.pem
/opt/jdk1.8.0_45/bin/keytool -importkeystore -srckeystore keystore-go.p12 -srcstoretype PKCS12 -destkeystore keystore-go -srcalias 1 -destalias cruise
