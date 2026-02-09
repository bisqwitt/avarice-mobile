package com.avaricious;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParticleManager {

    private static ParticleManager instance;

    public static ParticleManager I() {
        return instance == null ? instance = new ParticleManager() : instance;
    }

    private ParticleManager() {
    }

    private final List<ParticleEffect> emitters = new ArrayList<>();
    private final List<ParticleEffect> topLayerEmitters = new ArrayList<>();

    public void draw(SpriteBatch batch, float delta) {
        for(ParticleEffect particleEffect : emitters) {
            particleEffect.update(delta);
            particleEffect.draw(batch);
        }
        Set<ParticleEffect> dump = new HashSet<>();
        for(ParticleEffect particleEffect : emitters) {
            if(particleEffect.isComplete()) dump.add(particleEffect);
        }

        emitters.remove(dump);
    }

    public void drawTopLayer(SpriteBatch batch, float delta) {
        for(ParticleEffect particleEffect : topLayerEmitters) {
            particleEffect.update(delta);
            particleEffect.draw(batch);
        }
        Set<ParticleEffect> dump = new HashSet<>();
        for(ParticleEffect particleEffect : topLayerEmitters) {
            if(particleEffect.isComplete()) dump.add(particleEffect);
        }
        topLayerEmitters.remove(dump);
    }

    public void create(float x, float y, ParticleType type, float streak) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.03f);
        for(ParticleEmitter emitter : particle.getEmitters()) {
            emitter.getEmission().setHigh(streak * 60);
        }

        particle.setPosition(x, y);
        particle.start();
        emitters.add(particle);
    }

    public void create(float x, float y, ParticleType type) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.03f);

        particle.setPosition(x, y);
        particle.start();
        emitters.add(particle);
    }

    public void createTopLayer(float x, float y, ParticleType type) {
        ParticleEffect particle = new ParticleEffect();
        particle.load(type.getFile(),
            Gdx.files.internal("particles/pngs"));
        particle.scaleEffect(0.05f);

        particle.setPosition(x, y);
        particle.start();
        topLayerEmitters.add(particle);
    }

}

