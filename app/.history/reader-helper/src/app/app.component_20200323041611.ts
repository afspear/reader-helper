import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'reader-helper';

  constructor(private httpClient: HttpClient) {}

  onImage(image: any) {


    let encodedBase64Picture : any ;
    var file: File = image ;
    var reader: FileReader = new FileReader();
    reader.onloadend = ( e ) => {
    encodedBase64Picture = reader.result;
    }
    reader.readAsDataURL( file );


    const endpoint = 'http://localhost:7000/ocr';
    const formData: FormData = new FormData();
    formData.append('file', file, 'upload');
    return this.httpClient
      .post(endpoint, formData)
      .toPromise()
      .then()
      .catch((e) => console.log(e));
  }
}

