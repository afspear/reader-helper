export interface Ocr {
  responses: Response[];
}

export interface Response {
  textAnnotations:    TextAnnotation[];
  fullTextAnnotation: FullTextAnnotation;
}

export interface FullTextAnnotation {
  pages: Page[];
  text:  string;
}

export interface Page {
  property: ParagraphProperty;
  width:    number;
  height:   number;
  blocks:   Block[];
}

export interface Block {
  property:    ParagraphProperty;
  boundingBox: Bounding;
  paragraphs:  Paragraph[];
  blockType:   string;
  confidence:  number;
}

export interface Bounding {
  vertices: Vertex[];
}

export interface Vertex {
  x: number;
  y: number;
}

export interface Paragraph {
  property:    ParagraphProperty;
  boundingBox: Bounding;
  words:       Word[];
  confidence:  number;
}

export interface ParagraphProperty {
  detectedLanguages: PurpleDetectedLanguage[];
}

export interface PurpleDetectedLanguage {
  languageCode: Locale;
  confidence:   number;
}

export enum Locale {
  En = "en",
  ID = "id",
}

export interface Word {
  property:    WordProperty;
  boundingBox: Bounding;
  symbols:     Symbol[];
  confidence:  number;
}

export interface WordProperty {
  detectedLanguages: FluffyDetectedLanguage[];
}

export interface FluffyDetectedLanguage {
  languageCode: Locale;
}

export interface Symbol {
  property:    SymbolProperty;
  boundingBox: Bounding;
  text:        string;
  confidence:  number;
}

export interface SymbolProperty {
  detectedLanguages: FluffyDetectedLanguage[];
  detectedBreak?:    DetectedBreak;
}

export interface DetectedBreak {
  type: Type;
}

export enum Type {
  LineBreak = "LINE_BREAK",
  Space = "SPACE",
}

export interface TextAnnotation {
  locale?:      Locale;
  description:  string;
  boundingPoly: Bounding;
}

