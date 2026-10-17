#version 150

in vec4 vColor;
in vec2 vLocalPos;

out vec4 fragColor;

uniform vec2 Size;
uniform float Radius;

// Функция SDF для закругленного прямоугольника
float sdRoundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b;
    return max(max(q.x, q.y), (q.x + q.y + r) * 0.70710678);
}

void main() {
    vec2 size = Size / 2.0;
    float dist = sdRoundedBox(vLocalPos - size, size, Radius);
    float dw = fwidth(dist);
    float alpha = 1.0 - smoothstep(-dw, dw, dist);
    fragColor = vec4(vColor.rgb, vColor.a * alpha);
    if (fragColor.a < 0.005) {
        discard;
    }
}