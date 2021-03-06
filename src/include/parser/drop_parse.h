//===----------------------------------------------------------------------===//
//
//                         Peloton
//
// drop_parse.h
//
// Identification: src/include/parser/peloton/drop_parse.h
//
// Copyright (c) 2015-16, Carnegie Mellon University Database Group
//
//===----------------------------------------------------------------------===//


#pragma once

#include "common/types.h"

#include "common/logger.h"

#include "parser/abstract_parser.h"

namespace peloton {
namespace parser {

class DropParse : public AbstractParse {
 public:
  DropParse() = delete;
  DropParse(const DropParse &) = delete;
  DropParse &operator=(const DropParse &) = delete;
  DropParse(DropParse &&) = delete;
  DropParse &operator=(DropParse &&) = delete;

/*
  explicit DropParse(DropStmt *drop_node) {
    entity_type = ENTITY_TYPE_TABLE;

    ListCell *object_item;
    List *object_list = drop_node->objects;
    ListCell *subobject_item;
    List *subobject_list;

    // Value
    foreach(object_item, object_list){
      subobject_list = (List *)lfirst(object_item);

      foreach(subobject_item, subobject_list) {
        ::Value *value = (::Value *)lfirst(subobject_item);
        LOG_INFO("Table : %s ", strVal(value));
        entity_name = std::string(strVal(value));
        missing = drop_node->missing_ok;
      }
    }
  }
*/

  inline ParseNodeType GetParseNodeType() const { return PARSE_NODE_TYPE_DROP; }

  const std::string GetEntityName() { return entity_name; }

  const std::string GetInfo() const { return "DropParse"; }

  std::string GetTableName() { return entity_name; }

  bool IsMissing() { return missing; }

 private:
  // Type of entity
  EntityType entity_type = ENTITY_TYPE_INVALID;

  // Name of entity
  std::string entity_name;

  bool missing;
};

}  // namespace parser
}  // namespace peloton
