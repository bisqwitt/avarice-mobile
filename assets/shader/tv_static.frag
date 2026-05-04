#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;

uniform float u_time;
uniform vec2 u_resolution;

// A fast pseudo-random hash
float hash(vec2 p) {
    p = fract(p * vec2(443.897, 441.423));
    p += dot(p, p + 19.19);
    return fract(p.x * p.y);
}

void main() {
    vec2 uv = v_texCoords;

    // Scanline flicker — subtle horizontal bands drifting down
    float scanline = sin((uv.y + u_time * 0.08) * 80.0) * 0.04;

    // Per-frame noise grain
    float grain = hash(uv * u_resolution + floor(u_time * 24.0));

    // Occasional full-row glitch bursts
    float glitchRow = step(0.993, hash(vec2(floor(uv.y * 40.0), floor(u_time * 4.0))));
    float glitch    = glitchRow * hash(vec2(uv.x * 200.0, u_time * 30.0));

    // DEFAULT
    vec3 dark    = vec3(0.04, 0.10, 0.12);   // deep teal-black
    vec3 warm    = vec3(0.85, 0.55, 0.10);   // golden amber  (bells, 7s)
    vec3 pop     = vec3(0.75, 0.10, 0.08);   // slot-machine red
    vec3 mid     = vec3(0.10, 0.45, 0.18);   // clover green
//
//    // INFERNO
//    vec3 dark = vec3(0.08, 0.02, 0.02);
//    vec3 warm = vec3(0.95, 0.40, 0.05);
//    vec3 pop  = vec3(1.00, 0.80, 0.10);
//    vec3 mid  = vec3(0.60, 0.10, 0.02);
//
//    // BLUE
//    vec3 dark = vec3(0.02, 0.04, 0.12);
//    vec3 warm = vec3(0.10, 0.70, 0.90);
//    vec3 pop  = vec3(0.05, 0.30, 0.80);
//    vec3 mid  = vec3(0.00, 0.50, 0.60);

//    // GREEN
//    vec3 dark = vec3(0.02, 0.06, 0.02);
//    vec3 warm = vec3(0.30, 0.95, 0.20);
//    vec3 pop  = vec3(0.60, 1.00, 0.00);
//    vec3 mid  = vec3(0.05, 0.40, 0.05);
//
//    // VIOLET
//    vec3 dark = vec3(0.06, 0.02, 0.12);
//    vec3 warm = vec3(0.85, 0.70, 0.20);
//    vec3 pop  = vec3(0.60, 0.10, 0.80);
//    vec3 mid  = vec3(0.30, 0.05, 0.55);
//
//    // LIGHT BLUE
//    vec3 dark = vec3(0.03, 0.05, 0.10);
//    vec3 warm = vec3(0.70, 0.85, 1.00);
//    vec3 pop  = vec3(0.40, 0.60, 0.90);
//    vec3 mid  = vec3(0.20, 0.40, 0.70);

    // Mix noise through the palette
    vec3 color = mix(dark, warm, grain * 0.35);
//    color      = mix(color, pop,  step(0.82, grain) * 0.5);
//    color      = mix(color, mid,  step(0.91, grain) * 0.4);

    // Apply scanlines + glitch on top
    color += scanline;
    color += glitch * warm * 0.15;

    // Vignette — darkens corners like an old CRT
    float vignette = uv.x * (1.0 - uv.x) * uv.y * (1.0 - uv.y);
    vignette = clamp(pow(vignette * 16.0, 0.4), 0.0, 1.0);
    color *= vignette;

    gl_FragColor = vec4(color, 1.0);
}
