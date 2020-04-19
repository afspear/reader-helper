import { Component, ViewChild, ElementRef, OnInit } from '@angular/core';
import { HttpClient, } from '@angular/common/http';
import { Ocr } from './ocr/ocr';
import { Area } from './ocr/area';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent  {
  constructor(private httpClient: HttpClient) {}

  imageBase64: string;

  areas: Area[] = [];

  selectedArea: Area;

  audio;

  config = {
    fade: false,
    alwaysOn: true,
    neverOn: false,
    fill: true,
    fillColor: '#ffffff',
    fillOpacity: 0.4,
    stroke: true,
    strokeColor: '#4d0ec0',
    strokeOpacity: 1,
    strokeWidth: 3,
    shadow: true,
    shadowColor: '#000000',
    shadowOpacity: 0.8,
    shadowRadius: 10
  };

  onImage(image: any) {
    this.areas = [];

    this.imageBase64 = image;
    const endpoint = 'http://localhost:7000/ocr';
    const formData: FormData = new FormData();
    formData.append('file', this.dataURItoBlob(image), 'upload');
    return this.httpClient
      .post<Ocr>(endpoint, formData)
      .toPromise()
      .then(ocr => {
        ocr.responses.forEach(response => {
          response.fullTextAnnotation.pages.forEach(page => {
            page.blocks.forEach(block => {
              const area = new Area();
              block.boundingBox.vertices.forEach(verticie => {
                area.coords.push(verticie.x);
                area.coords.push(verticie.y);
              });
              const paragraphWords: string[] = [];
              block.paragraphs.forEach(paragraph => {
                paragraph.words.forEach(word => {
                  const words: string[] = [];
                  word.symbols.forEach(symbol => {
                    words.push(symbol.text);
                  });
                  paragraphWords.push(words.join(''));

                });
              });
              area.alt = paragraphWords.join(' ');
              this.areas.push(area);
            });
          });
        });
      })
      .catch((e) => console.log(e));
  }


getMp3File(text: string) {
this.httpClient.get('http://localhost:7000/text-to-speech?text=' + text , {responseType: 'blob'})
  .toPromise()
  .then(response => {
    this.downLoadFile(response, 'audio/ogg');
});
}

/**
* Method is use to download file.
* @param data - Array Buffer data
* @param type - type of the document.
*/
downLoadFile(data: any, type: string) {
let blob = new Blob([data], { type: type});
let url = window.URL.createObjectURL(blob);
this.audio = new Audio();
this.audio.src = url;
this.audio.load();
this.audio.play();
}



  dataURItoBlob(dataURI) {
    const byteString = window.atob(dataURI);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const int8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < byteString.length; i++) {
      int8Array[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([int8Array], { type: 'image/jpeg' });
    return blob;
 }

 show(area: Area) {
   this.selectedArea = area;
   this.getMp3File(area.alt);
 }



}
