/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.biz)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.persistence.lqmg.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.everit.persistence.lqmg.LQMGException;
import org.everit.persistence.lqmg.internal.schema.xml.AbstractNamingRuleType;
import org.everit.persistence.lqmg.internal.schema.xml.ClassNameRuleType;
import org.everit.persistence.lqmg.internal.schema.xml.LQMGType;
import org.everit.persistence.lqmg.internal.schema.xml.NamingRulesType;
import org.everit.persistence.lqmg.internal.schema.xml.RegexRuleType;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.mysema.query.sql.codegen.NamingStrategy;

/**
 * The configuration container used to code generation.
 */
public class ConfigurationContainer {

  /**
   * {@link ConfigValue} with <code>null</code>.
   */
  private static class NullConfigValue extends ConfigValue<AbstractNamingRuleType> {

    NullConfigValue() {
      super(null, null, null);
    }

  }

  private final Map<ConfigKey, ConfigValue<? extends AbstractNamingRuleType>> cache =
      new HashMap<ConfigKey, ConfigValue<? extends AbstractNamingRuleType>>();

  private final Map<ConfigKey, String> classNameCache = new HashMap<ConfigKey, String>();

  private final Map<ConfigKey, ConfigValue<ClassNameRuleType>> classNameRuleMap =
      new HashMap<ConfigKey, ConfigValue<ClassNameRuleType>>();

  private final JAXBContext jaxbContext;

  private final Map<ConfigKey, ConfigValue<ClassNameRuleType>> mainClassNameRuleMap =
      new HashMap<ConfigKey, ConfigValue<ClassNameRuleType>>();

  private final Map<ConfigKey, ConfigValue<RegexRuleType>> mainRegexRuleMap =
      new HashMap<ConfigKey, ConfigValue<RegexRuleType>>();

  private final Map<String, Pattern> patternsByRegex = new HashMap<String, Pattern>();

  private final Set<ConfigPath> processedConfigs = new HashSet<ConfigPath>();

  private final Map<ConfigKey, ConfigValue<RegexRuleType>> regexRuleMap =
      new HashMap<ConfigKey, ConfigValue<RegexRuleType>>();

  /**
   * Constructor.
   */
  public ConfigurationContainer() {
    try {
      jaxbContext = JAXBContext.newInstance(LQMGType.class.getPackage().getName(), this.getClass()
          .getClassLoader());
    } catch (JAXBException e) {
      throw new LQMGException("Could not create JAXBContext for configuration", e);
    }
  }

  /**
   * Adds a configuration.
   */
  public void addConfiguration(final ConfigPath configPath) {
    if (!processedConfigs.add(configPath)) {
      // If the config file is already processed, just return
      return;
    }
    Bundle bundle = configPath.bundle;
    String resource = configPath.resource;
    URL configurationURL;
    if (bundle == null) {
      try {
        configurationURL = new File(configPath.resource).toURI().toURL();
      } catch (MalformedURLException e) {
        throw new LQMGException(
            "Could not read configuration from path " + configPath.resource, e);
      }
    } else {
      BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
      ClassLoader classLoader = bundleWiring.getClassLoader();
      configurationURL = classLoader.getResource(resource);
    }

    if (configurationURL == null) {
      throw new LQMGException(
          "Configuration file not found on the specified path '" + resource + "' in bundle "
              + bundle.toString(),
          null);
    }
    LQMGType lqmgType = unmarshalConfiguration(configurationURL);
    processLQMGType(lqmgType, resource, bundle);
  }

  private <T extends AbstractNamingRuleType> void addValueToConfigMap(final ConfigKey configKey,
      final ConfigValue<T> configValue,
      final Map<ConfigKey, ConfigValue<T>> configMap) {

    ConfigValue<? extends AbstractNamingRuleType> cachedValue = cache.get(configKey);
    if (cachedValue != null) {
      cache.remove(configKey);
    }

    ConfigValue<T> existingValue = configMap.get(configKey);
    if (existingValue != null) {
      StringBuilder sb = new StringBuilder("Configuration is defined more than once: ").append(
          configKey.toString()).append("\n");

      Bundle bundle = configValue.bundle;
      if (bundle != null) {
        sb.append("  Bundle: ").append(bundle.toString()).append("; ");
      }
      sb.append("Path: ").append(configValue.configurationXMLPath).append("\n");

      Bundle existingValueBundle = existingValue.bundle;
      if (existingValueBundle != null) {
        sb.append("  Bundle: ").append(existingValueBundle.toString()).append("; ");
      }
      sb.append("Path: ").append(existingValue.configurationXMLPath);

      throw new LQMGException(sb.toString(), null);
    }
    configMap.put(configKey, configValue);
  }

  /**
   * Finds a {@link ConfigValue} based on schemaName and entityName.
   */
  public ConfigValue<? extends AbstractNamingRuleType> findConfigForEntity(final String schemaName,
      final String entityName) {
    ConfigKey configKey = new ConfigKey(schemaName, entityName);
    ConfigValue<? extends AbstractNamingRuleType> configValue = cache.get(configKey);
    if (configValue != null) {
      if (configValue instanceof NullConfigValue) {
        return null;
      } else {
        return configValue;
      }
    }

    configValue = findEntityConfigInMap(schemaName, entityName, mainClassNameRuleMap);
    if (configValue != null) {
      return configValue;
    }
    configValue = findRegexRuleInMap(schemaName, entityName, mainRegexRuleMap);
    if (configValue != null) {
      return configValue;
    }
    configValue = findEntityConfigInMap(schemaName, entityName, classNameRuleMap);
    if (configValue != null) {
      return configValue;
    }

    return findRegexRuleInMap(schemaName, entityName, regexRuleMap);
  }

  private ConfigValue<ClassNameRuleType> findEntityConfigInMap(final String schemaName,
      final String entityName,
      final Map<ConfigKey, ConfigValue<ClassNameRuleType>> map) {

    ConfigKey configKey = new ConfigKey(schemaName, entityName);
    ConfigValue<ClassNameRuleType> configValue = map.get(configKey);
    if (configValue != null) {
      return configValue;
    }
    if (schemaName != null) {
      ConfigKey nullSchemaConfigKey = new ConfigKey(null, entityName);
      return map.get(nullSchemaConfigKey);
    }
    return null;
  }

  private ConfigValue<RegexRuleType> findRegexRuleInMap(final String schemaName,
      final String entityName, final Map<ConfigKey, ConfigValue<RegexRuleType>> map) {

    List<ConfigValue<RegexRuleType>> result = new ArrayList<ConfigValue<RegexRuleType>>();
    if (schemaName != null) {
      for (Entry<ConfigKey, ConfigValue<RegexRuleType>> entry : map.entrySet()) {
        ConfigKey entryKey = entry.getKey();
        ConfigValue<RegexRuleType> configValue = entry.getValue();
        String regex = configValue.namingRule.getRegex();
        if (schemaName.equals(entryKey.schemaName) && matches(regex, entityName)) {
          result.add(configValue);
        }
      }
    }
    validateConfigValueResultSize(schemaName, entityName, result);
    if (result.size() == 1) {
      return result.get(0);
    }

    // No schema matched record, trying to search on records where schema is not defined
    for (Entry<ConfigKey, ConfigValue<RegexRuleType>> entry : map.entrySet()) {
      ConfigValue<RegexRuleType> configValue = entry.getValue();
      RegexRuleType regexRule = configValue.namingRule;
      if ((regexRule.getSchema() == null) && matches(regexRule.getRegex(), entityName)) {
        result.add(configValue);
      }
    }

    validateConfigValueResultSize(schemaName, entityName, result);
    if (result.size() == 1) {
      return result.get(0);
    }
    return null;
  }

  private Pattern getPatternByRegex(final String regex) {
    Pattern pattern = patternsByRegex.get(regex);
    if (pattern == null) {
      pattern = Pattern.compile(regex);
      patternsByRegex.put(regex, pattern);
    }
    return pattern;
  }

  private boolean matches(final String regex, final String value) {
    Pattern pattern = getPatternByRegex(regex);
    Matcher matcher = pattern.matcher(value);

    return matcher.matches();
  }

  private void processClassNameRuleType(final String xmlConfigurationPath, final Bundle bundle,
      final AbstractNamingRuleType lqmgAbstractEntity) {
    ClassNameRuleType lqmgEntity = (ClassNameRuleType) lqmgAbstractEntity;
    ConfigKey configKey = new ConfigKey(lqmgEntity.getSchema(),
        lqmgEntity.getEntity());
    ConfigValue<ClassNameRuleType> configValue =
        new ConfigValue<ClassNameRuleType>(lqmgEntity,
            bundle, xmlConfigurationPath);

    validatePackage(configValue);
    if (bundle == null) {
      addValueToConfigMap(configKey, configValue, mainClassNameRuleMap);
    } else {
      addValueToConfigMap(configKey, configValue, classNameRuleMap);
    }
  }

  private void processLQMGType(final LQMGType lqmgType, final String xmlConfigurationPath,
      final Bundle bundle) {
    String defaultPackageName = lqmgType.getDefaultPackage();
    String defaultSchemaName = lqmgType.getDefaultSchema();

    NamingRulesType entities = lqmgType.getNamingRules();
    if (entities == null) {
      return;
    }

    List<AbstractNamingRuleType> entityAndEntitySet = entities.getClassNameRuleAndRegexRule();
    for (AbstractNamingRuleType lqmgAbstractEntity : entityAndEntitySet) {
      if (lqmgAbstractEntity.getPackage() == null) {
        lqmgAbstractEntity.setPackage(defaultPackageName);
      }

      if (lqmgAbstractEntity.getSchema() == null) {
        lqmgAbstractEntity.setSchema(defaultSchemaName);
      }

      if (lqmgAbstractEntity.getPrefix() == null) {
        lqmgAbstractEntity.setPrefix(lqmgType.getDefaultPrefix());
      }

      if (lqmgAbstractEntity.getSuffix() == null) {
        lqmgAbstractEntity.setSuffix(lqmgType.getDefaultSuffix());
      }

      if (lqmgAbstractEntity instanceof ClassNameRuleType) {
        processClassNameRuleType(xmlConfigurationPath, bundle, lqmgAbstractEntity);
      } else if (lqmgAbstractEntity instanceof RegexRuleType) {
        processRegexRuleType(xmlConfigurationPath, bundle, lqmgAbstractEntity);
      } else {
        throw new IllegalArgumentException(
            "Unsupported naming rule type [" + lqmgAbstractEntity + "]");
      }
    }
  }

  private void processRegexRuleType(final String xmlConfigurationPath, final Bundle bundle,
      final AbstractNamingRuleType lqmgAbstractEntity) {
    RegexRuleType lqmgEntitySet = (RegexRuleType) lqmgAbstractEntity;
    ConfigKey configKey = new ConfigKey(lqmgEntitySet.getSchema(),
        lqmgEntitySet.getRegex());
    ConfigValue<RegexRuleType> configValue = new ConfigValue<RegexRuleType>(lqmgEntitySet,
        bundle, xmlConfigurationPath);

    validatePackage(configValue);
    if (bundle == null) {
      addValueToConfigMap(configKey, configValue, mainRegexRuleMap);
    } else {
      addValueToConfigMap(configKey, configValue, regexRuleMap);
    }
  }

  /**
   * Resolves the class name of the specified schemaName and entityName based on the
   * {@link NamingStrategy}.
   */
  public String resolveClassName(final String schemaName, final String entityName,
      final NamingStrategy namingStrategy) {
    ConfigKey key = new ConfigKey(schemaName, entityName);
    String className = classNameCache.get(key);
    if (className != null) {
      return className;
    }
    ConfigValue<? extends AbstractNamingRuleType> configValue =
        findConfigForEntity(schemaName, entityName);
    if (configValue == null) {
      return null;
    }

    AbstractNamingRuleType namingRule = configValue.namingRule;
    if (namingRule instanceof RegexRuleType) {
      RegexRuleType regexRule = (RegexRuleType) namingRule;
      String regex = regexRule.getRegex();
      String replacement = regexRule.getReplacement();
      Pattern pattern = getPatternByRegex(regex);
      Matcher matcher = pattern.matcher(entityName);
      String replacedEntityName = matcher.replaceAll(replacement);
      className = namingStrategy.getClassName(replacedEntityName);
    } else if (namingRule instanceof ClassNameRuleType) {
      className = ((ClassNameRuleType) namingRule).getClazz();
    }

    String prefix = namingRule.getPrefix();
    if (prefix != null) {
      className = prefix + className;
    }

    String suffix = namingRule.getSuffix();
    if (suffix != null) {
      className = className + suffix;
    }

    classNameCache.put(key, className);
    return className;
  }

  private void throwMultipleMatchingRegexException(final String schemaName, final String entityName,
      final List<ConfigValue<RegexRuleType>> matchingConfigs) {

    StringBuilder sb = new StringBuilder("Cannot decide which configuration should be applied to '")
        .append(entityName).append("' entity of '").append(schemaName)
        .append("' schema. Found matchings:\n");

    for (ConfigValue<RegexRuleType> configValue : matchingConfigs) {
      RegexRuleType namingRule = configValue.namingRule;
      sb.append("  Bundle: ").append(configValue.bundle).append("; XMLPath: ")
          .append(configValue.configurationXMLPath).append("; Schema: ")
          .append(namingRule.getSchema())
          .append("; Regex: ").append(namingRule.getRegex());
    }
    throw new LQMGException(sb.toString(), null);
  }

  private LQMGType unmarshalConfiguration(final URL configurationURL) {
    Unmarshaller unmarshaller;
    try {
      unmarshaller = jaxbContext.createUnmarshaller();

      @SuppressWarnings("unchecked")
      JAXBElement<LQMGType> rootElement =
          (JAXBElement<LQMGType>) unmarshaller.unmarshal(configurationURL);
      return rootElement.getValue();
    } catch (JAXBException e) {
      throw new LQMGException(
          "Could not unmarshal LQMG configuration: " + configurationURL.toExternalForm(), e);
    }
  }

  private void validateConfigValueResultSize(final String schemaName, final String entityName,
      final List<ConfigValue<RegexRuleType>> result) {
    if (result.size() > 1) {
      throwMultipleMatchingRegexException(schemaName, entityName, result);
    }
  }

  private void validatePackage(final ConfigValue<? extends AbstractNamingRuleType> configValue) {
    AbstractNamingRuleType namingRule = configValue.namingRule;
    if ((namingRule.getPackage() != null) && !"".equals(namingRule.getPackage().trim())) {
      return;
    }
    StringBuilder sb = new StringBuilder("Missing java package: ConfigValue [bundle=")
        .append(configValue.bundle).append(", configurationXMLPath=")
        .append(configValue.configurationXMLPath).append(", namingRule.schema=")
        .append(namingRule.getSchema()).append(", ");

    if (namingRule instanceof RegexRuleType) {
      sb.append("namingRule.regex=").append(((RegexRuleType) namingRule).getRegex());
    } else if (namingRule instanceof ClassNameRuleType) {
      sb.append("namingRule.class=").append(((ClassNameRuleType) namingRule).getClazz());
    }
    sb.append("].");
    throw new LQMGException(sb.toString(), null);
  }
}
