varying highp vec2 vTexCoord;
uniform highp sampler2D sTexture;

uniform highp float S;
uniform highp float H;
uniform highp float L;

highp vec3 rgb2hsl(highp vec3 rgb) {
  highp float r = rgb.x;
  highp float g = rgb.y;
  highp float b = rgb.z;
  highp float max = max(r, max(g, b));
  highp float min = min(r, min(g, b));
  highp float h;
  highp float s;
  highp float l = (max + min) / 2.0;
  if (max == min) {
    h = 0.0;
    s = 0.0;
  } else {
    highp float d = max - min;
    s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);
    if (max == r) {
      h = ((g - b) / d + (g < b ? 6.0 : 0.0)) / 6.0;
    } else if (max == g) {
      h = ((b - r) / d + 2.0) / 6.0;
    } else {
      h = ((r - g) / d + 4.0) / 6.0;
    }
  }
  return vec3(h, s, l);
}

highp float hue2rgb(highp float f1, highp float f2, highp float hue) {
    if (hue < 0.0)
        hue += 1.0;
    else if (hue > 1.0)
        hue -= 1.0;
    highp float res;
    if ((6.0 * hue) < 1.0)
        res = f1 + (f2 - f1) * 6.0 * hue;
    else if ((2.0 * hue) < 1.0)
        res = f2;
    else if ((3.0 * hue) < 2.0)
        res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;
    else
        res = f1;
    return res;
}



highp vec3 hsl2rgb(highp vec3 hsl) {
    highp vec3 rgb;
    if (hsl.y == 0.0) {
        rgb = vec3(hsl.z);
    } else {
        highp float f2;
        if (hsl.z < 0.5)
            f2 = hsl.z * (1.0 + hsl.y);
        else
            f2 = hsl.z + hsl.y - hsl.y * hsl.z;
        highp float f1 = 2.0 * hsl.z - f2;
        rgb.r = hue2rgb(f1, f2, hsl.x + (1.0/3.0));
        rgb.g = hue2rgb(f1, f2, hsl.x);
        rgb.b = hue2rgb(f1, f2, hsl.x - (1.0/3.0));
    }
    return rgb;
}

highp vec3 rgb2hsv(highp vec3 c){
    highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    highp vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    highp vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    highp float d = q.x - min(q.w, q.y);
    highp float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}
highp vec3 hsv2rgb(highp vec3 c){
    highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}


void main() {
    highp vec4 rgba = texture2D(sTexture,vTexCoord);
    highp vec3 hsl = rgb2hsv(rgba.xyz);
    if(S != 1.0)hsl.y = hsl.y*S;
    if(H != 0.0)hsl.x = H;
    if(hsl.x<0.0)hsl.x = hsl.x+1.0;
    else if(hsl.x>1.0)hsl.x = hsl.x-1.0;
    if(L != 1.0)hsl.z = hsl.z*L;
    highp vec3 rgb = hsv2rgb(hsl);
    gl_FragColor = vec4(rgb,rgba.w);
}