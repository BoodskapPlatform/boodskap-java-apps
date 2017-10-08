# java-camera

A simple utility application that takes image and video snapshots from the attached camera and upload them to Boodskap IoT Platform

#### Usage: java -jar java-camera-1.0.0.jar

#### Example Configuration

*Configuration can be found under ${HOME}/.camera/config.json*

```json
{
  "domainKey": "YOUR-DOMAIN-KEY",
  "apiKey": "YOUR-API-KEY",
  "deviceId": "Office Camera 01",
  "deviceModel": "BSKP-PICAM",
  "firmwareVersion": "1.0.0",
  "mode": "SNAP",
  "publisher": "UDP",
  "interval": 15000,
  "framePerMinite": 30,
  "imageFormat": "jpeg",
  "streamFormat": "mp4",
  "imageWidth": 200,
  "imageHeight": 200,
  "imageQuality": 0.5,
  "cameraResolutions": {},
  "availableResolutions": [
    "UXGA",
    "WQXGA",
    "WQHD",
    "XGA",
    "QQVGA",
    "SXGA",
    "CIF",
    "VGA",
    "PAL",
    "HD720",
    "QVGA",
    "QXGA",
    "WXGA",
    "HVGA",
    "SVGA"
  ],
  "availableFormats": [
    "JPG",
    "jpg",
    "bmp",
    "BMP",
    "gif",
    "GIF",
    "WBMP",
    "png",
    "PNG",
    "jpeg",
    "wbmp",
    "JPEG"
  ],
  "mqttUrl": "tcp://mqtt.boodskap.io:1883",
  "udpHost": "udp.boodskap.io",
  "udpPort": 5555,
  "httpUrl": "http://api.boodskap.io"
}
```