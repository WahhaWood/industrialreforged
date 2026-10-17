#version 150

in vec4 vColor;
in vec2 vLocalPos;

out vec4 fragColor;

uniform vec2 Size;
uniform float Radius;
uniform float BorderThickness;

// Та же SDF-функция
float sdRoundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + vec2(r);
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
}

void main() {
    vec2 p = vLocalPos - Size * 0.5;
    vec2 b = Size * 0.5;
    float halfBorder = BorderThickness * 0.5;

    float dOuter = sdRoundedBox(p, b, Radius);
    float dInner = sdRoundedBox(p, b - vec2(halfBorder), max(Radius - halfBorder, 0.0));

    float aa = fwidth(dOuter);
    float alpha = smoothstep(-aa, aa, -dOuter) * smoothstep(-aa, aa, dInner);

    fragColor = vec4(vColor.rgb, vColor.a * alpha);
    if (fragColor.a < 0.01) {
        discard;
    }
}